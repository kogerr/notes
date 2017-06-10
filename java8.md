# Java 8
.stream().filter(Predicate )
.stream().mapToInt(ToIntFunction )
.stream().mapToInt(transaction -> transaction.getPrice()).max()
.stream().map(transaction -> transaction.getPrice()).max(Integer::compare)

* sum: .mapToInt(transaction -> transaction.getPrice()).sum()
* .stream().max((a, b) -> Integer.compare(a.getPrice(), b.getPrice())).get.getStreet();
* belső iterálás: .stream().forEach(action):
.stream().filter(t -> t.getCity().equals("CITRUS HEIGHTS"))
.forEach(t -> {System.out.println(t)});
* listának is van .forEach methodja
* csak ha van ilyen, Optional.ifPresent(consumer):
.strem().filter().findFirst().ifPresent(System.out::println);
* rendezni az adatokat: sorted() vagy vagy soreted(comparator)
.stream().sorted((a, b) -> Integer.compare(a.getPrice(), b.getPrice()))
.limit(5).collect(Collectors.toList());

**Laziness:** csak akkor fut le, ha kiértékelik a kifejezést

* __default method:__ implementálja subclassokba kérdés nélkül.
kompatibilitáshoz jó
_Diamond class inheritance_ többszörös öröklés problémája - visszaköszön
ilyeknor ki kell írni, pl: LoggerFacade.super.log()



