// tools/jenkins/manifests/pipelines/test-integration.groovy
pipeline {
    agent {
        label 'jenkins-agent'
    }
    
    environment {
        SONAR_PROJECT_KEY = "test-project"
    }
    
    stages {
        stage('Test Vault Integration') {
            steps {
                withVault([
                    vaultCredentialId: 'vault-token',
                    vaultPath: 'secret/data/jenkins/sonarqube'
                ]) {
                    sh '''
                        echo "Vault Integration Test"
                        echo "Testing Vault Connection..."
                        if [ -n "$VAULT_TOKEN" ]; then
                            echo "Successfully retrieved Vault token"
                            # Test reading from Vault directly
                            VAULT_ADDR="http://vault.security-tools.svc.cluster.local:8200" \
                            vault read secret/data/jenkins/sonarqube
                        else
                            echo "Failed to retrieve Vault token"
                            exit 1
                        fi
                    '''
                }
            }
        }
        
        stage('Test SonarQube Connection') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        echo "SonarQube Integration Test"
                        echo "SonarQube URL: ${SONAR_HOST_URL}"
                        curl -u "${SONAR_TOKEN}:" "${SONAR_HOST_URL}/api/system/status"
                    '''
                }
            }
        }
        
        stage('Print Environment') {
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
    }
}