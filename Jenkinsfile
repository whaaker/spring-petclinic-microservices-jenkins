pipeline {
    agent any
    environment {
        // App Settings
        project_name="PetClinic-Jenkins" //DTP Root Project
        project_repo="https://github.com/parasoft/spring-petclinic-microservices.git" //git repo of project
        app_short="PC" //petclinic
        services_list = "spring-petclinic-api-gateway,spring-petclinic-vets-service,spring-petclinic-visits-service,spring-petclinic-customers-service"

        // Jenkins UID:GID
        jenkins_uid=995
        jenkins_gid=991

        // Parasoft Licenses
        ls_url="${PARASOFT_LS_URL}" //https\://dtp:8443
        ls_user="${PARASOFT_LS_USER}" //admin
        ls_pass="${PARASOFT_LS_PASS}"
        
        // Parasoft Common Settings
        ctp_url="${PARASOFT_CTP_URL}" //https://[ip from thanos VM]:8080
        ctp_user="${PARASOFT_CTP_USER}" //admin
        ctp_pass="${PARASOFT_CTP_PASS}"
        dtp_url="${PARASOFT_DTP_URL}" //https://dtp:8443
        dtp_user="${PARASOFT_DTP_USER}" //admin
        dtp_pass="${PARASOFT_DTP_PASS}"
        dtp_publish="${PARASOFT_DTP_PUBLISH}" //false
        buildId="${app_short}-${BUILD_TIMESTAMP}" //do it dynamically instead
        
        // Parasoft Jtest Settings
        jtestSAConfig="jtest.builtin://Recommended Rules"
        jtestSessionTag="PetClinicJenkins-Jtest"
        unitCovImage="PetClinic_All;PetClinic_UnitTest"

        // Functional Test Settings
        functionalCovImage="PetClinic_All;PetClinic_Selenium"
    }
    stages {
        stage('Setup') {
            steps {
                deleteDir()

                // setup the workspace
                sh  '''
                    # Clone this repository & PetClinic repository into the workspace
                    mkdir petclinic-jenkins;
                    git clone https://github.com/whaaker/spring-petclinic-microservices-jenkins.git petclinic-jenkins;

                    mkdir petclinic;
                    git clone ${project_repo} petclinic;

                    # Download coverage package
                    curl -LO -u ${ctp_user}:${ctp_pass} ${ctp_url}/em/coverageagent/java_agent_coverage.zip
                    unzip java_agent_coverage.zip

                    # Debugging
                    pwd;
                    tree .;
                '''

                // add agent.jar into all microservices projects
                script {
                    def servicesArray = services_list.split(',')
                    for (def dir in servicesArray) {
                        sh "cp jtest_agent/agent.jar ./petclinic/${dir}/src/test/resources/coverage/agent.jar"
                    }
                }

                // Debugging
                sh 'tree ./petclinic'

                // Update CTP JSON in local workspace
                script {
                    def jsonFilePath = "${env.WORKSPACE}/petclinic-jenkins/petclinic-docker/ctp.json"
                    def jsonFile = readFile(jsonFilePath)
                    def json = new groovy.json.JsonSlurper().parseText(jsonFile)

                    // debug
                    echo "${json}"

                    // Update the 'buildId' and 'coverageImages' properties
                    def servicesArray = services_list.split(',')
                    for (def dir in servicesArray) {
                        def instance = json.components.find { it.instances[0].coverage.dtpProject == dir }.instances[0].coverage
                        instance.buildId = "${dir}-${BUILD_TIMESTAMP}"
                        instance.coverageImages = "${functionalCovImage}"
                    }

                    // Serialize the updated JSON
                    def updatedJson = new groovy.json.JsonBuilder(json).toPrettyString()

                    // Write the updated JSON back to the file
                    writeFile(file: jsonFilePath, text: updatedJson)
                }

                sh 'cat ./petclinic-jenkins/petclinic-docker/ctp.json'
                
                // Prepare the jtestcli.properties file
                sh '''
                    # Set Up and write .properties file
                    echo $"
                    parasoft.eula.accepted=true
                    jtest.license.use_network=true
                    jtest.license.network.edition=custom_edition
                    jtest.license.custom_edition_features=Jtest, Static Analysis, Flow Analysis, OWASP Rules, CWE Rules, PCI DSS Rules, DISA STIG Rules, Security Rules, Automation, Desktop Command Line, DTP Publish, Coverage, Unit Test, Unit Test Bulk Creation, Unit Test Tier 1, Unit Test Tier 2, Unit Test Tier 3, Unit Test Tier 4, Unit Test Spring Framework, Change Based Testing
                    license.network.use.specified.server=true
                    license.network.auth.enabled=true
                    license.network.url=${ls_url}
                    license.network.user=${ls_user}
                    license.network.password=${ls_pass}

                    report.associations=false
                    report.coverage.images=${unitCovImage}
                    report.scontrol=full
                    scope.local=true
                    scope.scontrol=true
                    scope.xmlmap=false
                    
                    scontrol.git.exec=git
                    scontrol.rep1.git.branch=main
                    scontrol.rep1.git.url=${project_repo}
                    scontrol.rep1.type=git

                    build.id=${buildId}
                    session.tag=${jtestSessionTag}
                    dtp.url=${dtp_url}
                    dtp.user=${dtp_user}
                    dtp.password=${dtp_pass}" > ./petclinic-jenkins/jtest/jtestcli.properties
                    '''
            }
        }
        stage('Quality Scan') {
            when {
                expression {
                    return false;
                }
            }
            steps {
                // Execute the build with Jtest Maven plugin in docker        
                sh '''
                    # Run Maven build with Jtest tasks via Docker
                    docker run \
                    -u ${jenkins_uid}:${jenkins_gid} \
                    --rm -i \
                    --name jtest \
                    -v "$PWD/petclinic:/home/parasoft/jenkins/petclinic" \
                    -v "$PWD/petclinic-jenkins:/home/parasoft/jenkins/petclinic-jenkins" \
                    -w "/home/parasoft/jenkins/petclinic" \
                    --network=demo-net \
                    $(docker build -q ./petclinic-jenkins/jtest) /bin/bash -c " \

                    # Compile the project and run Jtest Static Analysis
                    mvn compile \
                    jtest:jtest \
                    -DskipTests=true \
                    -s /home/parasoft/.m2/settings.xml \
                    -Djtest.settings='../petclinic-jenkins/jtest/jtestcli.properties' \
                    -Djtest.config='${jtestSAConfig}' \
                    -Djtest.report=./target/jtest/sa \
                    -Djtest.showSettings=true \
                    -Dproperty.dtp.project=${project_name} \
                    -Dproperty.report.dtp.publish=${dtp_publish}; \
                    "
                    '''
                echo '---> Parsing 10.x static analysis reports'
                recordIssues(
                    tools: [parasoftFindings(
                        localSettingsPath: '$PWD/petclinic-jenkins/jtest/jtestcli.properties',
                        pattern: '**/target/jtest/sa/*.xml'
                    )],
                    unhealthy: 100, // Adjust as needed
                    healthy: 50,   // Adjust as needed
                    minimumSeverity: 'HIGH', // Adjust as needed
                    // qualityGates: [[
                    //     threshold: 10,
                    //     type: 'TOTAL_ERROR',
                    //     unstable: true
                    // ]],
                    skipPublishingChecks: true // Adjust as needed
                )
            }
        }
        stage('Unit Test') {
            when {
                expression {
                    return false;
                }
            }
            steps {
                // Execute the build with Jtest Maven plugin in docker
                sh '''
                    # Run Maven build with Jtest tasks via Docker
                    docker run \
                    -u ${jenkins_uid}:${jenkins_gid} \
                    --rm -i \
                    --name jtest \
                    -v "$PWD/petclinic:/home/parasoft/jenkins/petclinic" \
                    -v "$PWD/petclinic-jenkins:/home/parasoft/jenkins/petclinic-jenkins" \
                    -w "/home/parasoft/jenkins/petclinic/${dir}" \
                    --network=demo-net \
                    $(docker build -q ./petclinic-jenkins/jtest) /bin/bash -c " \

                    # Compile the test sources and run unit tests with Jtest
                    mvn test-compile \
                    jtest:agent \
                    test \
                    jtest:jtest \
                    -s /home/parasoft/.m2/settings.xml \
                    -Dmaven.test.failure.ignore=true \
                    -Djtest.settings='../petclinic-jenkins/jtest/jtestcli.properties' \
                    -Djtest.config='builtin://Unit Tests' \
                    -Djtest.report=./target/jtest/ut \
                    -Djtest.showSettings=true \
                    -Dproperty.dtp.project=${dir} \
                    -Dproperty.report.dtp.publish=${dtp_publish}; \
                    "
                    '''
                echo '---> Parsing 10.x unit test reports'
                script {
                    step([$class: 'XUnitPublisher', 
                        // thresholds: [failed(
                        //     failureNewThreshold: '0', 
                        //     failureThreshold: '0')
                        // ],
                        tools: [[$class: 'ParasoftType', 
                            deleteOutputFiles: true, 
                            failIfNotNew: false, 
                            pattern: '**/target/jtest/ut/*.xml', 
                            skipNoTestFiles: true, 
                            stopProcessingIfError: false
                        ]]
                    ])
                }
            }
        }
        stage('Package-CodeCoverage') {
            when {
                expression {
                    return true;
                }
            }
            steps {
                // Execute the build with Jtest Maven plugin in docker
                sh '''
                    # Run Maven build with Jtest tasks via Docker
                    docker run \
                    -u ${jenkins_uid}:${jenkins_gid} \
                    --rm -i \
                    --name jtest \
                    -v "$PWD/petclinic:/home/parasoft/jenkins/petclinic" \
                    -v "$PWD/petclinic-jenkins:/home/parasoft/jenkins/petclinic-jenkins" \
                    -w "/home/parasoft/jenkins/petclinic" \
                    --network=demo-net \
                    $(docker build -q ./petclinic-jenkins/jtest) /bin/bash -c " \

                    # Package the application with the Jtest Monitor
                    mvn package jtest:monitor \
                    -s /home/parasoft/.m2/settings.xml \
                    -Dmaven.test.skip=true \
                    -Djtest.settings='../petclinic-jenkins/jtest/jtestcli.properties' \
                    -Djtest.showSettings=true \
                    -Dproperty.report.dtp.publish=${dtp_publish}; \
                    "

                    # check petclinic/target permissions
                    #ls -la ./petclinic/target

                    # Unzip monitor.zip
                    mkdir monitor
                    unzip -q ./petclinic/target/jtest/monitor/monitor.zip -d .
                    ls -ll
                    ls -ll monitor
                    '''
            }
        }
        stage('Deploy-CodeCoverage') {
            when {
                expression {
                    return true;
                }
            }
            steps {
                // check running containers and deploy
                sh '''
                    pwd;
                    ls -ll petclinic/spring-petclinic-visits-service/src/test/resources/coverage;
                    docker-compose -f ./petclinic-jenkins/petclinic-docker/docker-compose-coverage.yml down || true;
                    sleep 10s;
                    
                    docker-compose -f ./petclinic-jenkins/petclinic-docker/docker-compose-coverage.yml up -d;
                    sleep 80s;

                    
                    '''
                // Health check coverage agents
                sh '''
                    curl -iv --raw http://localhost:8050/status
                    curl -iv --raw http://localhost:8051/status
                    curl -iv --raw http://localhost:8052/status
                    curl -iv --raw http://localhost:8053/status
                    '''

                // update CTP with yaml script upload
                sh """
                    # upload yaml file to CTP
                    curl -X 'PUT' \
                        -u ${ctp_user}:${ctp_pass} \
                        '${ctp_url}/em/api/v3/environments/32/config' \
                        -H 'accept: application/json' \
                        -H 'Content-Type: application/json' \
                        -d @${env.WORKSPACE}/petclinic-jenkins/petclinic-docker/ctp.json
                    """
            }
        }
                
        stage('Functional Test') {
            when {
                expression {
                    return false;
                }
            }
            steps {
                // Setup workspace and soatestcli.properties file
                sh  '''
                    # TODO: The following examples (integrated with CTP REST API):
                    #   - [High-code, open source tool use case]
                    #        - Upgrade, CIBC, Bank of America
                    #        - Maven fail-safe, TestNG, Selenium
                    #        - 'main' sources & 'test' sources are separate repositories
                    #        - Docker image with Java, Maven, Selenium, and Chrome setup to execute functional tests
                    #   - [GUI commercial tool case]
                    #        - SOAtest web functional test
                    #        - Calling CTP REST APIs as Setup/Teardown
                    #        - Using docker image with browser installed
                    '''
                
                // echo '---> Parsing 9.x soatest reports'
                // script {
                //     step([$class: 'XUnitPublisher', 
                //         // thresholds: [failed(
                //         //     failureNewThreshold: '10', 
                //         //     failureThreshold: '10',
                //         //     unstableNewThreshold: '20', 
                //         //     unstableThreshold: '20')
                //         // ],
                //         tools: [[$class: 'ParasoftSOAtest9xType', 
                //             deleteOutputFiles: true, 
                //             failIfNotNew: false, 
                //             pattern: '**/soatest/report/*.xml', 
                //             skipNoTestFiles: true, 
                //             stopProcessingIfError: false
                //         ]]
                //     ])
                // }
            }
        }
        stage('Release') {
            steps {
                // Release the project
                sh  '''
                # Clean up
                
                '''
            }
        }
    }
    post {
        // Clean after build
        always {
            sh '''
                docker-compose -f ./petclinic-jenkins/petclinic-docker/docker-compose-coverage.yml down || true;
                sleep 10s;
                docker container prune -f;
                docker image prune -f;
            '''

            archiveArtifacts(artifacts: '''
                    **/target/**/*.war, 
                    **/target/**/*.jar, 
                    **/target/jtest/sa/**, 
                    **/target/jtest/ut/**, 
                    **/target/jtest/monitor/**, 
                    **/soatest/report/**''',
                fingerprint: true, 
                onlyIfSuccessful: true,
                excludes: '''
                    **/.jtest/**, 
                    **/metadata.json'''
            )

            deleteDir()
        }
    }
}