# GithubMonsterAPI


A new way to motivate you to write code and be more active! Check which D&D monster you are based on how many contributions you made in github. 
The more contributions in the last year, the stronger you are!

# Running

Execute this in the root of the project to build docker container and get rid of some build junk
```
sudo docker build -t github-monster-api .
sudo docker image prune --filter label=stage=githubMonsterApiBuilder
```
Then you can run it:
```
sudo docker run -d -p 8080:8080 --rm github-monster-api
```
# Usage 

API have one GET entrypoint at
```
http://localhost:8080/whoAmI?githubNickname={nickname}
```
Where ``nickname`` is yor github nickname.

### Example with curl
```
$ curl http://localhost:8080/whoAmI?githubNickname=illided
```
Output:
```
{
  "nickname" : "illided",
  "contributedLastYear" : 299,
  "monster" : {
    "name" : "Ettercap",
    "challenge_rating" : 2,
    "size" : "Medium",
    "type" : "monstrosity",
    "alignment" : "neutral evil",
    "armor_class" : 13,
    "hit_points" : 44,
    "strength" : 14,
    "dexterity" : 15,
    "constitution" : 13,
    "intelligence" : 7,
    "wisdom" : 12,
    "charisma" : 8
  }
}
```
