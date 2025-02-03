pipeline {
    agent {
        label 'jenkins-agent'
    }
    
    environment {
        SONAR_PROJECT_KEY = "test-project"
    }
    
    stages {
        stage('Test SonarQube Connection') {
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
        
        stage('Example Security Scan') {
            steps {
                sh '''
                    echo "Running security scan simulation..."
                    echo "✓ Checking dependencies"
                    echo "✓ Scanning for vulnerabilities"
                    echo "✓ Analyzing code quality"
                '''
            }
        }
        
        stage('Environment Info') {
            steps {
                sh '''
                    echo "Environment Information:"
                    echo "SONAR_PROJECT_KEY: ${SONAR_PROJECT_KEY}"
                    echo "Workspace: ${WORKSPACE}"
                    echo "Node Name: ${NODE_NAME}"
                '''
            }
        }
    }
    
    post {
        always {
            deleteDir()
        }
        success {
            echo 'Pipeline succeeded! All integrations are working.'
        }
        failure {
            echo 'Pipeline failed! Check the logs above for details.'
        }
    }
}