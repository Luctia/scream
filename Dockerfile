FROM openjdk:21-jdk-bookworm

ADD https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.zip /apache-jmeter.zip
RUN unzip /apache-jmeter.zip -d /
RUN mv /apache-jmeter-5.6.3 /jmeter

# Add the plugin manager
ADD https://repo1.maven.org/maven2/kg/apc/jmeter-plugins-manager/1.10/jmeter-plugins-manager-1.10.jar /jmeter/lib/ext/
RUN curl -o /jmeter/lib/cmdrunner-2.3.jar https://repo1.maven.org/maven2/kg/apc/cmdrunner/2.3/cmdrunner-2.3.jar
RUN java -cp /jmeter/lib/ext/jmeter-plugins-manager-1.10.jar org.jmeterplugins.repository.PluginManagerCMDInstaller

# Change workdir to /jmeter
WORKDIR /jmeter

# Install plugins
RUN ./bin/PluginsManagerCMD.sh install jpgc-tst=2.6,jpgc-casutg=2.10

# Copy config file into the container
COPY config.json config.json
