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
                withVault(configuration: [timeout: 60, vaultCredentialId: 'vault-token'],
                         vaultSecrets: [[path: 'secret/data/jenkins/sonarqube', secretValues: [[envVar: 'VAULT_SONAR_TOKEN', vaultKey: 'token']]]]) {
                    sh '''
                        echo "Vault Integration Test"
                        echo "Testing Vault Connection..."
                        if [ ! -z "$VAULT_SONAR_TOKEN" ]; then
                            echo "Successfully retrieved secret from Vault: Secret exists"
                            echo "Secret value matches SonarQube token: $([ "$VAULT_SONAR_TOKEN" == "$SONAR_TOKEN" ] && echo 'Yes' || echo 'No')"
                        else
                            echo "Failed to retrieve secret from Vault"
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