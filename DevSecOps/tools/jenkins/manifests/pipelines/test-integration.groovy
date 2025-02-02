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
                    withCredentials([usernamePassword(credentialsId: 'vault-token', usernameVariable: 'VAULT_USER', passwordVariable: 'TOKEN')]) {
                        withVault(
                            configuration: [
                                vaultCredentialId: 'vault-token',
                                vaultUrl: 'http://vault.security-tools.svc.cluster.local:8200',
                                engineVersion: 2
                            ]
                        ) {
                            sh '''
                                echo "Vault Integration Test"
                                echo "Testing Vault Connection..."
                                VAULT_TOKEN=$TOKEN vault read secret/jenkins/sonarqube
                            '''
                        }
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