# Proiect-Etapa-2-POO

**Nume**: Păunescu Mihai-Ionuț  
**Grupa**: 322CD  
**Data**: 10.01.2025

## Design Pattern-uri Utilizate
Am utilizat 4 design pattern-uri pentru dezvoltarea proiectului:

1. **Factory Pattern**:
    - **Locație**: Crearea obiectelor pentru comenzi, strategiile de cashback și planurile de servicii, în pachetele `command`, `model.plan` și `model.commerciant`.
    - **Motivație**: Simplifică procesul de inițializare a obiectelor și oferă flexibilitate pentru adăugarea de noi tipuri de comenzi, strategii sau planuri fără a modifica codul existent. Acest pattern centralizează procesul de creare a obiectelor, reducând duplicarea codului și îmbunătățind mentenabilitatea proiectului.

2. **Singleton**:
    - **Locație**: Gestionarea instanțelor serviciilor (ex. `AccountService`, `CardService`).
    - **Motivație**: Asigură existența unei singure instanțe a fiecărui serviciu pentru a reduce consumul de resurse și a centraliza logica. Acesta este utilizat pentru a evita crearea de instanțe multiple ale serviciilor critice, ceea ce permite acces global și coerent la resurse comune.

3. **Visitor Pattern**:
    - **Locație**: Realizarea operațiilor necesare fiecărei comenzi și crearea output-ului specific fiecărui tip de tranzacție, în pachetul `visitor`.
    - **Motivație**: Permite adăugarea de operațiuni noi fără a modifica structurile existente. Acest pattern decuplează logica operațiilor de structura datelor, facilitând extinderea aplicației prin adăugarea de noi tipuri de comenzi sau tranzacții cu impact minim asupra codului existent.

4. **Strategy Pattern**:
    - **Locație**: Implementarea strategiilor de cashback și a planurilor de servicii, în pachetele `model.plan` și `model.commerciant`.
    - **Motivație**: Facilitează schimbarea dinamică a comportamentului pentru cashback sau planuri în funcție de tipul de utilizator sau comerciant. Acest pattern promovează utilizarea unor clase separate pentru fiecare strategie, ceea ce face mai ușoară gestionarea și adăugarea de noi comportamente fără a afecta codul existent.

## Caracteristici Noi

### Utilizatori
Am adăugat informații suplimentare pentru utilizatori:
- **Data nașterii** (birthDate)
- **Ocupația** (occupation)
- **Tip de plan** (account management plan)

### Comerțianți
Am introdus comercianți cu următoarele caracteristici:
- **Cashback Strategy**:
    - **NrOfTransactions**: cashback progresiv bazat pe numărul tranzacțiilor.
    - **SpendingThreshold**: cashback procentual pe baza sumei totale cheltuite, influențat de tipul de plan al utilizatorului.

### Planuri de Servicii
Utilizatorii pot beneficia de diferite planuri de servicii:
- **Standard**
- **Student**
- **Silver**
- **Gold**

**Upgrade Plan:**
- Utilizatorii pot face upgrade la planuri superioare contra unei taxe.
- Upgrade automat pentru gold dacă utilizatorul efectuează 5 plăți de minimum 300RON.

### Conturi Noi
- **Savings Account:**
    - Permite retrageri către conturi clasice, cu limite de vârstă.
    - Include opțiunea de transfer valutar.
- **Business Account:**
    - Cont partajat pentru antreprenori, cu trei tipuri de utilizatori:
        - **Owner:** drepturi depline, inclusiv setarea limitelor.
        - **Manager:** poate efectua tranzacții și gestiona cardurile.
        - **Employee:** poate efectua tranzacții în limite prestabilite.

### Tranzacții Noi
- **Cash Withdrawal:** retragere numerar, comisioane aplicate în funcție de plan.
- **Custom Split Payment:** permite plăți personalizate în funcție de consumul fiecărui utilizator.

### Rapoarte Noi
- **Business Report:**
    - Tipuri: “Transaction” și “Commerciant”.
    - Include informații despre manageri, angajați și comercianți.

### Actualizări pentru Comenzi Existente
- **addFunds:** conform limitelor setate pentru utilizatorii business.
- **createCard:** carduri asociate utilizatorilor business, gestionate pe roluri.
- **deleteAccount:** doar owner-ul poate șterge conturi business.
- **sendMoney:** suportă transferuri către comercianți.
- **payOnline:** include validare pentru comercianți invalizi.
