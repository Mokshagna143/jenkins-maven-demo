pipeline {

    parameters {
        string(
            name: 'IMAGE_TAG',
            defaultValue: '',
            description: 'Docker image tag to deploy. Leave empty to use the current Jenkins build number.'
        )
    }

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
                    echo "Building Docker image:"
                    echo "jenkins-maven-demo:${BUILD_NUMBER}"

                    docker build \
                        -t jenkins-maven-demo:${BUILD_NUMBER} \
                        .
                '''
            }
        }

        stage('Docker Test') {
            steps {
                sh '''
                    echo "Testing Docker image..."

                    docker rm -f jenkins-maven-demo-test 2>/dev/null || true

                    docker run -d \
                        --name jenkins-maven-demo-test \
                        -p 8082:8080 \
                        jenkins-maven-demo:${BUILD_NUMBER}

                    echo "Waiting for application to start..."
                    sleep 5

                    echo "Testing application..."
                    curl -f http://localhost:8082

                    echo
                    echo "Docker application test successful."

                    docker rm -f jenkins-maven-demo-test
                '''
            }
        }

        stage('Load Image into Minikube') {
            steps {
                sh '''
                    echo "Loading Docker image into Minikube..."

                    sudo -u ubuntu -H minikube image load \
                        jenkins-maven-demo:${BUILD_NUMBER}

                    echo "Image loaded successfully."
                '''
            }
        }

        stage('Verify Minikube Image') {
            steps {
                sh '''
                    echo "Verifying image in Minikube..."

                    sudo -u ubuntu -H minikube image ls \
                        | grep "jenkins-maven-demo:${BUILD_NUMBER}"

                    echo "Minikube image verification successful."
                '''
            }
        }

        stage('Ansible Deploy to Kubernetes') {
            steps {
                sh '''
                    echo "Deploying image ${BUILD_NUMBER} to Kubernetes using Ansible..."

                    ANSIBLE_CONFIG=/opt/ansible-lab/ansible.cfg \
                    ansible-playbook \
                        ansible/k8s-deploy.yml \
                        -e "image_tag=${BUILD_NUMBER}"

                    echo "Ansible Kubernetes deployment successful."
                '''
            }
        }
    }

    post {

        always {
            echo 'Maven + Docker + Minikube + Ansible + Kubernetes pipeline finished.'
        }

        success {
            echo 'Maven build, Docker test, Minikube image loading, and Kubernetes deployment successful!'
        }

        failure {
            echo 'Maven, Docker, Minikube, or Kubernetes deployment stage failed!'
        }
    }
}
