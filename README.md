# Trixi XML Importer

Springboot java aplikace pro zpracování a uložení XML dat o obci a jejích částech do PostgreSQL databáze.

Technologie: Java 21, Spring Boot, Spring Data JPA, PostgreSQL, Docker, **StAX**

## Co aplikace dělá

Po spuštění aplikace:

1. Stáhne zazipovaná XML data.
2. Rozbalí ZIP archiv.
3. Pomocí StAX postupně zpracuje XML.
4. Extrahuje údaje o obci a jejích částech.
5. Uloží data do PostgreSQL.

Zpracovávají se pouze údaje potřebné pro požadované databázové tabulky.

Import je idempotentní.

## Spuštění

```bash
docker compose up --build
```

Aplikace po startu automaticky provede import dat.

## Datový model

### `Obec`

* `kod`
* `nazev`

### `CastObce`

* `kod`
* `nazev`
* `kod_obce`

`CastObce` je s `Obec` propojena pomocí vztahu `ManyToOne`.


