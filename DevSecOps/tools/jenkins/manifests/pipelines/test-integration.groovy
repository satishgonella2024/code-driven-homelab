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
                        // Test if environment variable is available
                        sh 'echo "Current VAULT_TOKEN: ${VAULT_TOKEN}"'
                        
                        withVault(
                            configuration: [
                                skipSslVerification: true,
                                timeout: 60,
                                vaultCredential: [
                                    id: 'vault-token',
                                    scope: 'GLOBAL'
                                ],
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
        
        // ... (keep other stages)
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