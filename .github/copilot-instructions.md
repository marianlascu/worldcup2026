# Instrucțiuni GitHub Copilot — WorldCup2026

## 1. Scopul proiectului
WorldCup2026 este o aplicație de predicții pentru Cupa Mondială 2026. Sarcina ta este să ajuți la înțelegerea, întreținerea și rafinarea proiectului existent, nu să îl reconstruiești din proprie inițiativă.

## 2. Tehnologii existente
- Java 21 (versiunea țintă din `pom.xml`); nu schimba versiunea fără aprobare.
- Spring Boot 3.5.0, Spring MVC, Thymeleaf și Spring Data JPA/Hibernate.
- Maven Wrapper: pe Windows folosește `./mvnw.cmd` sau `.\mvnw.cmd` din PowerShell.
- PostgreSQL pe un VPS este baza de date principală.
- H2 a fost folosit temporar pentru MVP; nu îl considera baza de date principală și nu îl reactiva fără aprobare.
- Integrare cu football-data.org API.

## 3. Regula fundamentală: analizează, propune, așteaptă aprobarea
1. La începutul oricărei sarcini, examinează strict fișierele și fluxurile relevante.
2. Explică starea actuală, cauza problemei sau oportunitatea identificată și impactul propunerii.
3. Prezintă un plan minimal, cu fișierele vizate și verificările necesare.
4. Nu modifica fișiere, dependențe, configurații, baze de date sau resurse externe înainte de aprobarea explicită a utilizatorului.
5. După aprobare, fă schimbări mici, verificabile și reversibile.
6. Nu interpreta o cerere de analiză sau audit drept permisiune de a implementa.

## 4. CONSOLE FIRST pentru UI/UX
- Pentru modificări vizuale sau de comportament în browser, propune mai întâi un prototip temporar în consola browserului, atunci când este tehnic posibil.
- Testează efectele în browser și solicită confirmarea utilizatorului.
- Abia apoi propune integrarea permanentă în Thymeleaf, CSS, JavaScript sau Java.
- Nu elimina funcționalități vechi și nu rescrie pagini întregi doar pentru simplificare.
- Menține interfața coerentă, responsive și accesibilă.

## 5. Protecția PostgreSQL de pe VPS
- Consideră baza PostgreSQL un sistem real, potențial cu date importante.
- Înainte de orice operație, distinge între interogări strict de citire și operații care pot modifica datele sau schema.
- Nu executa `INSERT`, `UPDATE`, `DELETE`, `TRUNCATE`, `DROP`, `ALTER`, migrații, seed-uri sau scripturi de populare fără aprobare explicită.
- Proiectul are configurat `spring.jpa.hibernate.ddl-auto=update`: avertizează că pornirea aplicației poate modifica schema; nu lansa aplicația sau teste cu efecte asupra bazei reale fără confirmare.
- Nu înlocui PostgreSQL cu H2 pentru a masca problemele de conectare.
- Pentru teste, propune izolarea datelor și o configurație de test adecvată, fără efecte asupra VPS-ului.

## 6. Securitate și secrete
- Nu afișa, copia în răspunsuri, loguri sau commituri parole, tokenuri API, chei sau alte secrete.
- Verifică dacă `application.properties` conține credențiale reale urmărite de Git; raportează riscul fără a reproduce valorile.
- Propune externalizarea secretelor în variabile de mediu sau fișiere locale excluse din Git, menținând compatibilitatea configurației existente.
- Nu publica date sensibile, nu schimba setări firewall sau SSL și nu deschide PostgreSQL către internet fără aprobare.
- Dacă un secret a fost deja publicat într-un repository, propune rotația lui și tratarea istoricului Git ca potențial compromis.

## 7. Integritatea funcțională a predictorului
- Păstrează regulile existente de predicții, scoruri, punctaj, clasamente, faze eliminatorii și drepturile utilizatorilor până când sunt înțelese și aprobate modificările.
- Nu inventa rezultate, meciuri, echipe, calendare sau răspunsuri API. Distinge datele confirmate de cele simulate.
- Pentru football-data.org, respectă configurația existentă, limitele API și tratarea erorilor; nu introduce apeluri suplimentare necontrolate.
- Verifică impactul schimbărilor asupra datelor istorice și a calculelor.

## 8. Calitate, testare și compatibilitate
- Păstrează Java 21 și structura Maven/Spring existentă.
- Nu actualiza automat Spring Boot, dependențele sau schema bazei de date.
- Înainte de modificare, identifică testele relevante și riscul lor pentru baza reală.
- Dacă testele pornesc contextul Spring și încearcă să se conecteze la VPS, semnalează acest lucru înainte de rulare.
- După schimbări aprobate, raportează explicit ce a fost testat, ce a trecut, ce a eșuat și ce nu a putut fi verificat.
- Nu ascunde erori prin dezactivarea testelor ori prin modificări de configurație fără să explici consecințele.

## 9. Git și controlul modificărilor
- Nu face `git commit`, `push`, `pull --rebase`, `reset --hard`, `clean -fd`, merge sau schimbări de branch fără cerere ori aprobare explicită.
- Nu șterge fișiere sau cod legacy fără inventarierea dependențelor și aprobarea utilizatorului.
- Înainte de fiecare implementare, arată fișierele afectate; după implementare, rezumă diferențele.
- Propune modificări mici și ușor de revenit la starea anterioară.

## 10. Stilul colaborării
- Comunică în **limba română**, clar și tehnic, fără explicații inutile.
- Nu presupune că o problemă este rezolvată doar pentru că buildul compilează.
- Pentru diagnostic, indică prima eroare cauzală, nu doar ultima excepție din stack trace.
- Separă explicit: **confirmat**, **probabil**, **neconfirmat**.
- Nu pretinde că ai inspectat fișiere, rulat comenzi sau verificat rezultate dacă nu ai făcut-o.
- Când utilizatorul cere audit, livrează constatări cu referințe la fișiere/metode, severitate, dovezi și un plan prioritizat — fără implementare automată.

## 11. Primul audit — mod READ-ONLY
La prima solicitare de audit:
1. Inventariază arhitectura și fluxurile existente.
2. Examinează logica de predicții și clasamente.
3. Verifică securitatea, secretele, JPA și conectivitatea către PostgreSQL.
4. Evaluează frontendul, performanța și testele.
5. Raportează problemele confirmate separat de ipoteze.
6. Propune un backlog etapizat: remedieri critice, stabilitate, securitate, UX și funcționalități.
7. Nu modifica niciun fișier și nu executa comenzi cu efecte secundare înainte de aprobare.
