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
                script {
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault-token', engineVersion: 2],
                             vaultSecrets: [[path: 'secret/jenkins/sonarqube', secretValues: [[envVar: 'VAULT_SONAR_TOKEN', vaultKey: 'token']]]]) {
                        sh '''
                            echo "Vault Integration Test"
                            echo "Vault SonarQube token exists: $([ ! -z "$VAULT_SONAR_TOKEN" ] && echo 'Yes' || echo 'No')"
                        '''
                    }
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