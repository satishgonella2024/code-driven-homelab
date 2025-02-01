# manifests/pipelines/test-integration.groovy
pipeline {
    agent any
    
    environment {
        SONAR_PROJECT_KEY = "test-project"
    }
    
    stages {
        stage('Verify Vault Integration') {
            steps {
                script {
                    withVault(configuration: [timeout: 60, vaultCredentialId: 'vault-token', engineVersion: 2],
                             vaultSecrets: [[path: 'secret/jenkins/sonarqube', secretValues: [[envVar: 'SONAR_TOKEN', vaultKey: 'token']]]]) {
                        sh '''
                            echo "Vault Integration Test"
                            echo "SonarQube Token exists: $( [ ! -z "$SONAR_TOKEN" ] && echo 'Yes' || echo 'No' )"
                        '''
                    }
                }
            }
        }
        
        stage('Verify SonarQube Integration') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        echo "SonarQube Integration Test"
                        echo "SonarQube URL: $SONARQUBE_URL"
                        echo "SonarQube Scanner will use the following URL: ${SONAR_HOST_URL}"
                    '''
                }
            }
        }
    }
    
    post {
        always {
            cleanWs()
        }
    }
}