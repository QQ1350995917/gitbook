
docker network create -d bridge local-docker-network-bridge

docker pull gitlab/gitlab-ce
docker run -d  -p 443:443 -p 81:81 -p 222:22 --name gitlab --restart always --network local-docker-network-bridge -v /Users/pwd/Document/docker/gitlab/config:/etc/gitlab -v /Users/pwd/Document/docker/gitlab/logs:/var/log/gitlab -v /Users/pwd/Document/docker/gitlab/data:/var/opt/gitlab gitlab/gitlab-ce
vim /Users/pwd/Document/docker/gitlab/config/gitlab.rb
https://www.jianshu.com/p/080a962c35b6

docker pull sonatype/nexus3
docker run -d -p 82:8081 --name nexus --privileged=true --network local-docker-network-bridge -v /Users/pwd/Document/docker/nexus:/nexus-data --restart=always sonatype/nexus3
https://blog.csdn.net/u012943767/article/details/79475718

# jenkins
docker pull jenkins/jenkins
docker run -d --name jenkins -p 83:8080 --network local-docker-network-bridge -v /Users/pwd/Document/docker/jenkins:/var/jenkins_home jenkins/jenkins
docker exec -it jenkins bash
cat /var/jenkins_home/secrets/initialAdminPassword

https://www.jianshu.com/p/12c9a9654f83

docker pull registry
docker run -d --network local-docker-network-bridge -v /Users/pwd/Document/docker/registry:/var/lib/registry -p 84:5000 --restart=always --name registry registry



======================ubuntu========================
vim /etc/docker/daemon.json

docker network create -d bridge local-docker-network-bridge

docker run -d -p 443:443 -p 81:80 -p 222:22 --network local-docker-network-bridge -v /var/lib/gitlab/config:/etc/gitlab -v /var/lib/gitlab/logs:/var/log/gitlab -v /var/lib/gitlab/data:/var/opt/gitlab --restart always --name gitlab gitlab/gitlab-ce

docker run -d -p 82:8081 --privileged=true -u 0 --network local-docker-network-bridge -v /var/lib/nexus:/nexus-data --restart=always --name nexus sonatype/nexus3

docker run jenkins -d -p 83:8080 --privileged=true -u 0 --network local-docker-network-bridge -v /var/lib/jenkins:/var/jenkins_home --restart always --name jenkins/jenkins
docker run -d -p 83:8080 --privileged=true -u 0 --network local-docker-network-bridge \
-v /var/lib/jenkins:/var/jenkins_home \
-v /usr/local/bin/java/java:/usr/local/jdk1.8 \
-v /usr/bin/git:/usr/bin/git \
-v /usr/local/bin/maven/maven:/usr/local/maven3 \
-v /var/run/docker.sock:/var/run/docker.sock \
--restart always --name jenkins jenkins/jenkins

docker run -d -p 84:5000 --network local-docker-network-bridge --privileged=true -v /var/lib/registry:/var/lib/registry --restart=always --name registry registry

