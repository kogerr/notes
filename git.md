# Git

### Source Control
#### központi Version Control System Server:
- megvan minden egy központi helyen, tudunk együtt dolgozni, backupot csinálni
- ilyen: SVM, CVS, (IBM) ClearCase
- ütközés problémája fölvetődik, erre ad megoldást minden VCS valahogy
- mindenki 1 konkrét verziót kiszed

#### Distributed Version Control System
- Git ilyen.
- minden gépen: teljes verzió adatbázis
- saját gépemen választhatok abból, amelyiken dolgozni szeretnék
- két gép között is lehetne kapcsolat szerver nélkül
- mivel csak időnként kommunikálunk a szerverrel, hatékonyabb a hálózati kommunikáció, sok minden lokális

### GIT
Short Story:
- a Linuxhoz találták ki
- először BitKeeper

What is git?
- distributed revision control and source code management system
- nem SVN
- mindig készül egy új snapshot a fáról

### Essentials
- Non-linear developement
  - egyszerre dolgozhatunk több különböző témán, feature-ön (branch-ek)
- Distributed developement: nem zavarjuk egymást
- Efficiency: sokkal kevesebb helyet foglal, mint az SVN - tömörít
  - hatékony hálózati műveletek (kevés, és tömörítve)

### Prerquisites
https://www.perforce.com/downloads/visual-merge-tool

### Basics
snapshots:
- kiindul az első verzióból, eltárólja az új verziókat a módosított fájlokból

### Versioning
- working directory-ban az van, amivel épp aktuálisan dolgozunk (checkout)
- Staging area/index: mi fog belekerülnia  következő kommitba
- Repository: egész verziózott adatbázis metadatával: .git directory

### Lifecycle
1. untracked - add ->
2. unmodified - edit ->
3. modified - stage ->
4. staged - commit -> unmodified

### Basic Commands
- `git init first-project` - létrejön könyvtár
- `git log` - kiírja commitokat
- `git log --oneline` - egyszerűbb
- `git log -3` - utolsó 3
- `git log --author name` / `git lof --committer name`
- `git help log` - kiadja az adott parancs dokumentációját
- `git show` - több mindenre jó. alapból megmutatja az utolsó commit diffjét
- `git diff` - jelenlegi diff local branchon

### gitignore
vim .gitignore
- bele nemkívánt file path-ja
- `git add .` -az összes fájlt hozzáadja
- `git rm` - töröl követett fájlt. kell commitolni is

### gitk
- `gitk &` - háttérben
- le lehet kérdezni, hogyan néz ki a repo

### gitconfig
3 szinten:
- system `/etc/gitconfig` - minden felhasználóra érvényes
- global `~/.gitconfig`
- local `.git/config` - projektre vonatkozó

- mindig alulról fölfele nézi, ha több van
- `git config --list` beállítot, beállítható dolgok (pl `mergetool=p4merge`)
- `git difftool`

### Object Database
- tárolja az összes tartalmat tömörítve
- SHA-1: 40 karakteres azonosító
- blob - fájlok
- tree: fájl struktúra (folderek)
- commitok
- ...

### Blob Tree
- fájlok és könyvtárak tömörítve:
  - fájl: méret és tartalom
  - tree: méret és tartalomjegyzék
- SH1 azonosító tartalomból generálódik: azonos fájloknál ugyanaz - egyszer lesz

### Commit:
- points to a tree
- szerző, committer, message és Parent commit

### Tag
- kiadja ügyfélnek az adott verziót, mindig arra a kommitra fog mutatni
- verzió tag, egy commitra hivatkozik +info: ki csinálta, comment...

### Data Model
- master branch is mutat mindig egy commitra
- commit-kor: csinál új commit-ot, master címkét átrakja rá (master file)
- remote: távoli repository, ez is mutat egy commitra
- HEAD: working areád hol van, ha csinálsz commitot, mihez fog fűződni (szülő)
  - master file-ra mutat, ami pedig commitra
- Objects are immutable
- References change

`git cat-file -p ******` - object database-ből SHA-1 eleje alapján
`git cat-file -t ******` - típusa

### Remotes
URLben:
- felhasználónév
- `@` után számítógép neve: `git.epam.com`
- `:` után projekt neve és `.git`

- `git remote add` - távoli repository fölvétele (clone= init + add)
- `git/refs/remotes` könyvtárba kerülnek a távoli branch-ek
- track-kelés: be lehet állítani egy távoli repot, amit követ és figyelmeztet
- rebase: átteszi legújabb commitra a cimkét

- új remote-nál:
  - `git init` név nélkül
  - commit
  - `git remote add...`
  - push

- `git remote -v` milyen címre mutatnak repók
- `git push -u origin master` -u: trackkelni fogja, távoli repo és branch
- fetch-kor átkerül a lokális origin/master címke
- `git rebase origin/master`
- `git pull --rebase` - egy lépésben ez

Pull request: leforkolt repository-ba pusholt commitra

### Git Branching
- elkülöníteni fejlesztéseket
- gitben csak egy 40 byte-os pointer, ami egy adott commitra mutat
- lokális művelet

- `git branch` létrehozni
- `git checkout` váltogatni
- `git merge` visszavezetni pl masterba

### Merge
- fast-forward: ha nem történt más commit, csak átrakja a master cimkét
- ha azóta volt változtatás, merge-öljük a kettőt össze (non-fast-forward)
- merge conflict feloldása:
  - 3-way merge: +base - az ahhoz képest történt változtatás kerül be
  - ha nem tudja, kérdez: `master|MERGING` akkor:
- `mergetool` és commit
- `git branch --merged` / `git branch --no-merched`
- `git branch -d branchname` / `git branch -D branchname`
- `git branch -m newname` - átnevezi
- `git show-branch` is kiírja brancheket és commitokat

### Rebasing
hogy a változtatások megjelenjenek masteren:
- visszamegy az elágazásig
- master utolsó commitjára aplikálja a branch commitjait (újrajátssza)
- `git rebase targetbranchname`

### Remotes
- `.git/refs/remotes` könyvtárban vannak a remote-ok, remote-onként egy mappa
- (`.git/refs/head`-ben a lokálisok)
- `.git/config` fájlban van konfigurálva a remote:
  - URL (távoli repo)
  - fetch: a gépünkön hova fogja lefetch-cselni

Mit lehet csinálni távoli branch-csel?
- fetch-cselni
- rebase-elni rá

- fetch után a gépen is ott lesznek az új commit-ok, arra mutat az o/m
- remote branchbe nem lehet commitolni
- ha kicsekkoljuk az o/m-t: detached HEAD: commitra mutat
- commit-ot is ki lehet checkout-olni
- `git push remotename newremotebranchname`
- `git push remotename localbranchname:newremotebranchname`
- `git push remotename :remotebranchname` törli távoli branchet (local megmarad)
- `git checkout remotebranchname` létrehoz local branch-et a remoteb.-ből

### Rebase vs Merge
- Rebase:
  - __+__ nincs elágazás
  - __+__ könnyű revertálni
  - __-__ nem látszik, honnan és mikor indult
  - __-__ nem működik együtt pull requesttel
  - __-__ átírjuk a múltat
- Merge
  - __+__ nyomonkövethetőség
  - __-__ tele tudja szemetelni a history-t merge commitokkal

_Guideline_: soha ne rebase-elj olyan commitot, amit már föltoltál public repóba

### Standard Git Branching
- _decvelop_ branchen folyik a munka
- _master_ branchen csak release-ek
- _release_ branch release előtt, innen merge-ölünk developba. ez -> master
- _hotfix_ branchek masterből release után
- _feature_ branchek a developból, ezeket is vissza kell merge-ölgetni

### Ancestry References
lehet relatívan hivatkozni
- `^` caret
  - `git show HEAD^`: 1st parent of the commit
  - `git show HEAD^2`: 2nd parent of the commit (többet is lehet merge-ölni)
- `~` tide
  - `git show HEAD~`: 1st parent of the commit
  - `git show HEAD~2`: 1st parent of the 1st parent of the commit

pl `git diff HEAD~2`

### Undoing
- `git commit --amend` change last commit (igazából új, mert immutable)
- `git revert HEAD` csinál egy ellencommit-ot: kitörli hozzáadott sorokat
- Reset:
  - soft: visszaállítja HEADet az előző commitra, de meghagyja a változtatásokat
  - mixed: minden non-staged állapotba kerül, amin volt módosítás
  - hard: a working area is visszaáll
- unmodify file: 
  - `git checkout -- filename` visszaállítja a fájl állapotát az utolsó commitra
  - `git checkout .` - visszaállítja az összes fájlt az utolsó commitra

### Stashing
félig elkészült dolgot nem akarsz commitolni, félrerakod polcra
- `git stash`
- `git stash apply` előszedi az utolsó stashelt módosítást és aplikálja

### Tagging
commit history pontjára címke, vissza tudsz oda lépni
- pl amikor ügyfélnek kiraksz egy verziót
- típusai:
  - lightweight tag: mint branch de nem lehet módosítani
  - annotated: létrejön object névvel, dátummal
- branchet lehet létrehozni tagből
- `git tag tagname`
- `git tag -a tagname -m message` annotált
- `git push remotename tagname` - ugyanúgy fölnyomni, mint brancheket
- `git tag` - listázza tageket
- `.git/refs/tags` mappában tárolja

### Housekeeping
- git tömörítve tárolja a fájlokat, minden objecthez van egy fájl object databas
- loose object format
- időnként "pack" fájlokba tömöríti - pl git push hatására, git gc, `git repack`
- `git prune` remove unreachable objects

### Git Directory
- `hooks/` - kliensoldali hookok, pl kommit előtt ellenőrizzen valamit
- `info` globálisan ignorált dolgokat itt is meg lehet adni
- `logs` visszanézni, milyen parancsok lettek leduttatva
- `objects` object database
- `refs/` pointerek commit objectekre
- `HEAD` working directory hova mutat
- `config`
- `index` staging area

### Git Workflows
- simán egyetlen repo
- blessed repo, integration manager és saját public branch mindenkinek
- Dictator & Lieutenant

### Commands
- plumbing commands (pl SHA-1 generálás, elkészíti bináris álományt)
- porcelán commandok (pl git add) ezeket ismerjük

