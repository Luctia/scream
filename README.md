# SCREAM - System for Cloud Resource Extraction And Motivation

## How-to
### Configuration
First, you must write your configuration in the GROWL DSL. Examples of this can be found in
`src/test/resources/growl/inputs`.

### Running with Kubernetes
First, a JAR must be built using `mvn package`. You can then build the Docker image using `docker build -t scream .`. 
Then you can start using SCREAM in Kubernetes by running `kubectl apply -f .\kubernetes\`.