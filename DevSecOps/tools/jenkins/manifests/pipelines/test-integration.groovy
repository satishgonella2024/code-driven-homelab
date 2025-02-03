pipeline {
    agent {
        label 'jenkins-agent'
    }
    
    environment {
        SONAR_PROJECT_KEY = "test-project"
        HARBOR_URL = "192.168.5.242"  // Use the LoadBalancer IP
        IMAGE_NAME = "devsecops/sample-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        echo "SonarQube Integration Test"
                        echo "SonarQube URL: ${SONAR_HOST_URL}"
                        curl -s -f -u "${SONAR_TOKEN}:" "${SONAR_HOST_URL}/api/system/status"
                        if [ $? -eq 0 ]; then
                            echo "Successfully connected to SonarQube"
                        else
                            echo "Failed to connect to SonarQube"
                            exit 1
                        fi
                    '''
                }
            }
        }
        
        stage('Build & Push Image') {
            agent {
                kubernetes {
                    yaml '''
                        apiVersion: v1
                        kind: Pod
                        spec:
                          containers:
                          - name: kaniko
                            image: gcr.io/kaniko-project/executor:latest
                            command:
                            - /busybox/cat
                            tty: true
                            volumeMounts:
                            - name: docker-config
                              mountPath: /kaniko/.docker
                          volumes:
                          - name: docker-config
                            configMap:
                              name: docker-config
                    '''
                }
            }
            steps {
                container('kaniko') {
                    sh """
                        /kaniko/executor \
                        --context=. \
                        --destination=${HARBOR_URL}/${IMAGE_NAME}:${IMAGE_TAG} \
                        --destination=${HARBOR_URL}/${IMAGE_NAME}:latest \
                        --insecure
                    """
                }
            }
        }
        
        stage('Security Scan') {
            agent {
                kubernetes {
                    yaml '''
                        apiVersion: v1
                        kind: Pod
                        spec:
                          containers:
                          - name: trivy
                            image: aquasec/trivy:latest
                            command:
                            - cat
                            tty: true
                    '''
                }
            }
            steps {
                container('trivy') {
                    sh '''
                        echo "Running Trivy vulnerability scan..."
                        trivy image --no-progress --severity HIGH,CRITICAL ${HARBOR_URL}/${IMAGE_NAME}:${IMAGE_TAG}
                    '''
                }
            }
        }
        
        stage('Report') {
            steps {
                sh '''
                    echo "Build Summary:"
                    echo "========================="
                    echo "Image: ${HARBOR_URL}/${IMAGE_NAME}:${IMAGE_TAG}"
                    echo "SonarQube Project: ${SONAR_PROJECT_KEY}"
                    echo "Build Number: ${BUILD_NUMBER}"
                '''
            }
        }
    }
    
    post {
        always {
            deleteDir()
            echo "Cleanup completed"
        }
    }
}