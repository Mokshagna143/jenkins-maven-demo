pipeline {

    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
            }
        }

        stage('Maven Version') {
            steps {
                sh 'mvn -version'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package'
            }
        }

        stage('List Artifacts') {
            steps {
                sh 'ls -lh target/'
            }
        }

        stage('Archive JAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t jenkins-maven-demo:${BUILD_NUMBER} \
                      .
                '''
            }
        }

        stage('Docker Test') {
            steps {
                sh '''
                    docker rm -f jenkins-maven-demo 2>/dev/null || true

                    docker run --name jenkins-maven-demo \
                      jenkins-maven-demo:${BUILD_NUMBER}

                    docker ps -a --filter name=jenkins-maven-demo
                '''
            }
        }

        stage('Ansible Deploy') {
            steps {
                sh '''
                    ANSIBLE_CONFIG=/opt/ansible-lab/ansible.cfg \
                    ansible-playbook /opt/ansible-lab/docker-deploy.yml \
                    -e "image_tag=${BUILD_NUMBER}"
                '''
            }
        }
    }

    post {

        always {
            echo 'Maven + Docker + Ansible pipeline finished.'
        }

        success {
            echo 'Maven build, Docker test, and Ansible deployment successful!'
        }

        failure {
            echo 'Maven, Docker, or Ansible pipeline failed!'
        }
    }
}
