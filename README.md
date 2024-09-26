# SCREAM - System for Cloud Resource Extraction And Motivation

## How-to
### Configuration
First, you must write your configuration in the GROWL DSL. Examples of this can be found in
`src/test/resources/growl/inputs`. When you have a complete configuration, you can run the application. This will
generate a `.jmx` file that will be used by JMeter.

### Running with Kubernetes
First, the JMeter Docker image must be built using `docker build -t scream .`. This will create a runnable Java Docker
image that will be used by Kubernetes. You can then deploy this image using the pre-made Kubernetes configuration files
in `/kubernetes`
