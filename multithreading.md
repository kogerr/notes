# Multithreading
motivation:
- better use of resources
  - multi-CPU
  - Blocking oprtation - nem áll a program, fut a többi szál, kihasználja gépet
- concurrent world - a világ működésének logikus leképezése
  - concurrent request
  - scheduled / batch (háttér) task - pl esti adatbázis optimalizáció
  - periodic updates (e.g., UI) - progress bar, óra

### Paralell Processing
- Multiprocessing - több exe fájl az oprendszer ütemezője váltogat
  - IPC: interprocess communication
- többszálú: egy process-nek több szála van, sokkal kevésbé különülnek el
  - shared heap, private stack: virtuális gépen fut minden program, saját
virtuális címtéren. ezen osztoznak a szálak. 

_stack_: minden szálnak sajátja: hívási láncok, paraméterek a stackre kerülnek,
lokális változók...

### Why Synchronize?
A programon belül szinkronizálni kell a memóriához való hozzáférést
- out-of-order execution: nem olyan sorrendben fognak lefutni, ahogy terveztük

