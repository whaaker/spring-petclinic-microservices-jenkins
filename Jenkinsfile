pipeline {
    agent any
    environment {
        // App Settings
        project_name="PetClinic-Jenkins" //DTP Project
        project_repo="https://github.com/parasoft/spring-petclinic-microservices.git" //git repo of project
        app_short="PC" //parabank

        //TODO
        app_name="petclinic-baseline" //docker container
        image="parasoft/parabank:baseline" //docker image
        app_port=8090
        app_cov_port=8050
        app_db_port=9021
        app_jms_port=63616
        //TODO
    
        // Docker host ip
        host_ip="172.17.0.1"

        // Jenkins UID:GID
        jenkins_uid=995
        jenkins_gid=991

        // Parasoft Licenses
        ls_url="${PARASOFT_LS_URL}" //https\://172.17.0.1:8443
        ls_user="${PARASOFT_LS_USER}" //admin
        ls_pass="${PARASOFT_LS_PASS}"
        
        // Parasoft Common Settings
        dtp_url="${PARASOFT_DTP_URL}" //https://172.17.0.1:8443
        dtp_user="${PARASOFT_DTP_USER}" //admin
        dtp_pass="${PARASOFT_DTP_PASS}"
        dtp_publish="${PARASOFT_DTP_PUBLISH}" //false
        buildId="${app_short}-${BUILD_TIMESTAMP}"
        
        // Parasoft Jtest Settings
        jtestSAConfig="jtest.builtin://Recommended Rules"
        jtestSessionTag="PetClinicJenkins-Jtest"
        unitCovImage="PetClinic_All;PetClinic_UnitTest"

        // Parasoft SOAtest Settings
        soatestConfig="soatest.user://Example Configuration"
        soatestSessionTag="PetClinicJenkins-SOAtest"
        soatestCovImage="PetClinic_All;PetClinic_SOAtest"
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

                    # Debugging
                    pwd;
                    tree .;
                    '''

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
                    dtp.password=${dtp_pass}
                    dtp.project=${project_name}" > ./petclinic-jenkins/jtest/jtestcli.properties
                    '''
            }
        }
        stage('Quality Scan') {
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
                    -Dproperty.report.dtp.publish=${dtp_publish}; \
                    "
                    '''
            }
        }
        stage('Unit Test') {
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
                    -Dproperty.report.dtp.publish=${dtp_publish}; \
                    "
                    '''
            }
        }
        stage('Package-CodeCoverage') {
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
                    #ls -ll
                    #ls -la monitor
                    '''
            }
        }
        stage('Process Reports') {
            steps {
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
        stage('Deploy-CodeCoverage') {
            steps {
                // deploy the project
                sh  '''
                    # Run Parabank-baseline docker image with Jtest coverage agent configured
                    docker run \
                    -d \
                    -u ${jenkins_uid}:${jenkins_gid} \
                    -p ${app_port}:8080 \
                    -p ${app_cov_port}:8050 \
                    -p ${app_db_port}:9001 \
                    -p ${app_jms_port}:61616 \
                    --env-file "$PWD/petclinic-jenkins/jtest/monitor.env" \
                    -v "$PWD/monitor:/home/docker/jtest/monitor" \
                    --network=demo-net \
                    --name ${app_name} \
                    $(docker build -q ./petclinic-jenkins/petclinic-docker)

                    # Health Check
                    sleep 15
                    docker ps -f name=${app_name}
                    curl -iv --raw http://localhost:${app_port}/parabank
                    curl -iv --raw http://localhost:${app_cov_port}/status
                    '''
            }
        }
                
        stage('Functional Test') {
            steps {
                // Setup workspace and soatestcli.properties file
                sh  '''
                    # Set Up and write .properties file
                    echo $"
                    parasoft.eula.accepted=true

                    license.network.use.specified.server=true
                    license.network.url=${ls_url}
                    license.network.auth.enabled=true
                    license.network.user=${ls_user}
                    license.network.password=${ls_pass}
                    soatest.license.use_network=true
                    soatest.license.network.edition=custom_edition
                    soatest.license.custom_edition_features=RuleWizard, Command Line, SOA, Web, Server API Enabled, Message Packs, Advanced Test Generation Desktop, Requirements Traceability, API Security Testing
                    
                    dtp.enabled=true
                    dtp.url=${dtp_url}
                    dtp.user=${dtp_user}
                    dtp.password=${dtp_pass}
                    dtp.project=${project_name}

                    build.id=${buildId}
                    session.tag=${soatestSessionTag}

                    report.dtp.publish=${dtp_publish}
                    report.associations=true
                    report.scontrol=full
                    scope.local=true
                    scope.scontrol=true
                    scope.xmlmap=false

                    application.coverage.enabled=true
                    application.coverage.agent.url=http\\://${host_ip}\\:${app_cov_port}
                    application.coverage.images=${soatestCovImage}

                    scontrol.git.exec=git
                    scontrol.rep1.git.branch=main
                    scontrol.rep1.git.url=${project_repo}
                    scontrol.rep1.type=git
                    " > ./petclinic-jenkins/soatest/soatestcli.properties
                    '''
                
                // Run SOAtestCLI from docker
                sh  '''
                    docker run \
                    -u ${jenkins_uid}:${jenkins_gid} \
                    --rm -i \
                    --name soatest \
                    -e ACCEPT_EULA=true \
                    -v "$PWD/petclinic-jenkins/soatest:/usr/local/parasoft/soatest" \
                    -w "/usr/local/parasoft" \
                    --network=demo-net \
                    $(docker build -q ./petclinic-jenkins/soatest) /bin/bash -c " \

                    # Create workspace directory and copy SOAtest project into it
                    mkdir -p ./soavirt_workspace/SOAtestProject/coverage_runtime_dir; \
                    cp -f -R ./soatest/SOAtestProject ./soavirt_workspace; \

                    cd soavirt; \

                    # SOAtest requires a project to be "imported" before you can run it
                    ./soatestcli \
                    -data /usr/local/parasoft/soavirt_workspace \
                    -settings /usr/local/parasoft/soatest/soatestcli.properties \
                    -import /usr/local/parasoft/soavirt_workspace/SOAtestProject/.project; \
                    
                    # Execute the project with SOAtest CLI
                    ./soatestcli \
                    -data /usr/local/parasoft/soavirt_workspace \
                    -resource /SOAtestProject \
                    -environment '172.17.0.1' \
                    -config '${soatestConfig}' \
                    -settings /usr/local/parasoft/soatest/soatestcli.properties \
                    -property application.coverage.runtime.dir=/usr/local/parasoft/soavirt_workspace/SOAtestProject/coverage_runtime_dir \
                    -report /usr/local/parasoft/soatest/report \
                    "
                    '''
                
                echo '---> Parsing 9.x soatest reports'
                script {
                    step([$class: 'XUnitPublisher', 
                        // thresholds: [failed(
                        //     failureNewThreshold: '10', 
                        //     failureThreshold: '10',
                        //     unstableNewThreshold: '20', 
                        //     unstableThreshold: '20')
                        // ],
                        tools: [[$class: 'ParasoftSOAtest9xType', 
                            deleteOutputFiles: true, 
                            failIfNotNew: false, 
                            pattern: '**/soatest/report/*.xml', 
                            skipNoTestFiles: true, 
                            stopProcessingIfError: false
                        ]]
                    ])
                }
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
            sh 'docker container stop ${app_name}'
            sh 'docker container rm ${app_name}'
            sh 'docker image prune -f'

            archiveArtifacts(artifacts: '''
                    **/target/**/*.war, 
                    **/target/**/*.jar, 
                    **/target/jtest/sa/**, 
                    **/target/jtest/ut/**, 
                    **/target/jtest/monitor/**, 
                    **/soatest/report/**''',
                fingerprint: true, 
                onlyIfSuccessful: true,
                excludes: '**/.jtest/**'
            )

            deleteDir()
        }
    }
}