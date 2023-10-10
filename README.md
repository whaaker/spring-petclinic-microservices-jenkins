# spring-petclinic-microservices-jenkins
##Spring PetClinic Microservices Jenkins Pipeline (AWS EC2, docker)
This repository is a working demonstration of Parasoft's Continuous Quality Platform integrated with the Spring PetClinic Microservices demo application.  The Jenkins Pipelines defined in this repository are fully portable, meaning your Jenkins server only requires:
- A connection to the Internet
- Docker installed on the Jenkins machine
- Access to a Parasoft License Server with valid "Automation Edition" licenses
- (Optional) Access to a Parasoft DTP Server

## AWS EC2 Notes:
- If using Jenkins running on EC2, where a jenkins:jenkins user was created and you're using the default node, review the jtest, soatest, and parabank-docker Dockerfile scripts to make sure the UID and GID settings match the UID:GID of your jenkins user.  Also check the Jenkinsfiles for the UID and GID settings to match.
- If using LSS or DTP on the same EC2 instance as Jenkins (not recommended for customers), instead of using "host.docker.internal" for the LSS and DTP URLs, use the docker host ip address.  (Typically: 172.17.0.1)
- The docker script is connecting all containers to an external docker bridge network named "demo-net".  Make sure the Jenkins EC2 instance or build node (docker host) has this docker network created: docker network create demo-net

## Jenkins Setup:
- Add the following Jenkins plugins: Pipeline.*, Build Timestamp Plugin, Timestamper, Parasoft Environment Manager, Parasoft Findings

## Jenkins Parameterized Pipeline Build Paramaters:
- PARASOFT_LS_URL
- PARASOFT_LS_USER
- PARASOFT_LS_PASS
- PARASOFT_DTP_URL
- PARASOFT_DTP_USER
- PARASOFT_DTP_PASS
- PARASOFT_DTP_PUBLISH
    - true/false

## Configure Jenkins Pipeline with the following:
- Jenkinsfile: Quality Scan, Unit Tests, Deploy with coverage, Functional Test
- Jenkinsfile.security: SAST, Deploy with coverage, DAST
- Jenkinsfile.deployonly: Deploy with coverage, ephemeral for 30 minutes, primed for manual testing in the future