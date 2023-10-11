# baseline image
FROM parasoft/jtest

ARG HOST_UID=995
ARG HOST_GID=991

## Permissions to create dirs
USER root
ARG USER_HOME_DIR=/home/parasoft

# Install Lombok for Jtest
## TODO

# Install JDK 17
RUN mkdir -p /opt/java
RUN curl -L -O https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_linux-x64_bin.tar.gz
RUN tar -xvzf openjdk-17.0.2_linux-x64_bin.tar.gz -C /opt/java
RUN rm openjdk-17.0.2_linux-x64_bin.tar.gz
ENV JAVA_HOME /opt/java/jdk-17.0.2
ENV PATH $PATH:$JAVA_HOME/bin
#RUN update-alternatives --set java ${JAVA_HOME}/bin/java

# Install MVN
## Set MVN args
ARG MAVEN_VERSION=3.9.5
ENV MAVEN_HOME=${USER_HOME_DIR}/apache-maven-${MAVEN_VERSION}
ARG BASE_URL=https://apache.osuosl.org/maven/maven-3/${MAVEN_VERSION}/binaries
RUN mkdir -p ${MAVEN_HOME} \
 && curl -fsSL -o /tmp/apache-maven.tar.gz ${BASE_URL}/apache-maven-${MAVEN_VERSION}-bin.tar.gz \
 && tar -xzf /tmp/apache-maven.tar.gz -C ${MAVEN_HOME} --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && ln -s ${MAVEN_HOME}/bin/mvn /usr/bin/mvn \
 && groupmod -g ${HOST_GID} parasoft \
 && usermod -u ${HOST_UID} -g ${HOST_GID} parasoft

ENV PATH="${MAVEN_HOME}/bin:${PATH}"

## Copy Deps
COPY jtestcli.properties /home/parasoft/jtestcli.properties
COPY .m2 /home/parasoft/.m2

## Fix permissions
RUN chown -h -R ${HOST_UID}:${HOST_GID} ${USER_HOME_DIR}

## Switch user back and configure MVN
USER parasoft
ENV MAVEN_CONFIG "${USER_HOME_DIR}/.m2"

# Define working directory.
WORKDIR ${USER_HOME_DIR}

# Define commonly used JAVA_HOME variables

# Define default command.
CMD ["mvn", "--version"]