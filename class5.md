# class5: sportsbetting howto
- típusa: enum

###### Bet

- `Outcome` listát tartalmaz
- egy meccshez több bet: 
  - ki nyeri
  - hány gólt rúgnak összesen
  - Ronaldo hány gólt fog rúgni
  - win/lose,draw
	
###### Result
- annyi `Outcome` for hozzá tartozni, ahány bet van
- nyertes `Outcome`-ok tartoznak a `Result`hoz

###### correct

- `TennisSportEvent` legyen a neve
- `Bet`be típus: enum
- Currency:
  - `of()` helyett `java.enum.valueOf`
  - `String s` változónak más nevet

- `Outcome` vagy `OutcomeOdd` vonatkozik a másikra majd kiderül
- `OutcomeOdd`ot nem fogom változtatni, mindig lesz másik (final! szerintem)
- `Wager` ne tegyen el `OutcomeOdd` másolatot, csak referenciát
- `Result`ban lesznek az `Outcome`-ok

timesheetet vezetni, hogy mennyit foglalkozok mivel

