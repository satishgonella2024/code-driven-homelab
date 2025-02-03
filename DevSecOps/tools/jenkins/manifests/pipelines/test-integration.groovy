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
                    withEnv(["VAULT_ADDR=http://vault.security-tools.svc.cluster.local:8200"]) {
                        echo "Testing Vault connection at: ${env.VAULT_ADDR}"
                        withVault(
                            configuration: [
                                skipSslVerification: true,
                                vaultCredentialId: 'vault-token',
                                vaultUrl: env.VAULT_ADDR
                            ],
                            vaultSecrets: [
                                [
                                    path: 'secret/data/jenkins/sonarqube',
                                    secretValues: [
                                        [envVar: 'VAULT_SONAR_TOKEN', vaultKey: 'token']
                                    ]
                                ]
                            ]
                        ) {
                            sh '''
                                echo "Vault Integration Test"
                                echo "Testing Vault Connection..."
                                echo "Using Vault at: $VAULT_ADDR"
                                
                                if [ ! -z "$VAULT_SONAR_TOKEN" ]; then
                                    echo "Successfully retrieved secret from Vault"
                                    echo "Token is present and not empty"
                                else
                                    echo "Failed to retrieve secret from Vault"
                                    exit 1
                                fi
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
                        if curl -s -f -u "${SONAR_TOKEN}:" "${SONAR_HOST_URL}/api/system/status"; then
                            echo "Successfully connected to SonarQube"
                        else
                            echo "Failed to connect to SonarQube"
                            exit 1
                        fi
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
                    echo "Jenkins Home: ${JENKINS_HOME}"
                '''
            }
        }
    }
    
    post {
        always {
            deleteDir() // Using deleteDir instead of cleanWs
        }
        success {
            echo 'Pipeline succeeded! All integrations are working.'
        }
        failure {
            echo 'Pipeline failed! Check the logs above for details.'
        }
    }
}