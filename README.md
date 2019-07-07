# AuctionHouse

To Do IMPORTANT:


To Do MINOR:
- Fare reload automatico quando crei asta
- Formattare data nella list view e nella card


Per poter buildare e runnare correttamente il progetto bisogna:
1. Creare un nuovo db schema con MySQL copiando il contenuto del file "docs->AuctionHouseDBSchema"
2. Modificare il file "resources->Hibernate->hibernate.cfg.xml" agendo sull'url, in particolare sul nome dell'istanza del progetto, stando attenti che coincida a quella assegnata precedentemente su MySQL, modificare anche username e password inserendo quelli dell'account con i permessi di read e write
3. Runnare inizalmente "ServerTextualApplication" e successivamente "Main"
4. Una volta concluse tutte le operazione per chiudere il server digitare nel terminale "3" affinche' vengano chiuse tutte le connessioni correttamente