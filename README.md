# Debri
## Git flow model 설명
[강의 영상]<br>
[Git flow model - 생활코딩](https://www.youtube.com/watch?v=EzcF6RX8RrQ)

[참고 자료]<br>
[우아한 형제들 - 우린 Git-flow를 사용하고 있어요](https://techblog.woowahan.com/2553/)

**branch 종류**
> * master - 항상 실행 가능한 상태 유지
> * develop - 개발용
> * feature - 기능 단위 개발용
> * release - master로 옮기기전 검수용
> * hotfix - 급하게 고쳐야 하는 코드 디버깅용

### 기능 개발
- develop branch에서 시작
1. ```git checkout -b feature/[개발기능]```으로 feature branch 생성 및 이동
2. feature/[개발기능]에서 개발 진행 및 작업 종료 후 commit and push
    1. 이때 commit은 최소한으로
3. 개발이 완료되면 ```git checkout develop```으로 이동
4. ```git merge --no-ff feature/[개발기능]```으로 develop에 병합
5. ```git branch -d feature/[개발기능]```으로 이용이 끝난 브렌치 제거


### 개발이 끝난 기능 병합
- develop branch에서 시작
1. ```git checkout -b release/[version]```으로 release branch 생성 및 이동
2. ```git merge develop```으로 지금까지 개발 내용 저장
    1. 만약 수정할 내용 있으면 release/[version]에서 바로 바로 수정
3. ```git checkout master```로 master branch로 이동
    1. master branch로의 병합 전에는 항상 코드 리뷰를 부탁할 것!
4. ```git merge --no-ff release/[version]``` 을 통해 release/[version]를 master에 병합
5. ```git checkout develop```으로 develop branch로 이동
6.  ```git merge --no-ff release/[version]``` 을 통해 release/[version]를 develop에 병합
7. ```git branch -d release/[version]```으로 이용이 끝난 release/[version] branch 제거

### hotfix 이용
- master branch에서 오류 발생시
1. ```git checkout -b hotfix/[hotfix내용]```으로 hotfix branch 생성 및 이동
2. hotfix branch 내에서 오류 픽스
3. ```git checkout -b release/[version]```으로 release branch 생성 및 이동
    1. 만약 현재 사용중인 release branch가 있다면 그 branch로 이동
4. ```git merge --no-ff hotfix/[hotfix내용]```으로 hotfix 내용 적용
5. ```git checkout master```로 master branch로 이동
6. ```git merge --no-ff release/[version]```으로 hotfix 내용 적용 