Mihaila Alin-Florin
343C1

Detalii implementare:

    * Preprocesarea datelor:
        Am folosit csv-ul cu caile spre fiecare imagine in parte, am folosit pandas pentru a construi data frame-ul
        si am adaugat inca o coloana pentru label, am pus 1/0 in caz ca am pozitiv sau negativ in calea respectiva a imaginii.

        Am folosit din keras ImageDataGenerator si am incarcat toate imaginile cu ajutorul generatorului,
        si am grupat imaginile in cate 64 de batch-uri.

    * Construirea retelei
        Am folosit keras cu tensorflow ca backend si am construit 4 arhitecturi diferite pentru aceasta tema,
        prima fiind cea simpla din enuntul temei, a mai fost nevoie sa adaug un strat Flatten inainte de ultimul
        FC, deoarece era nevoie sa se reduca dimensiunea inputului pentru ultimul FC.

        A doua arhitectura, este construita pornind de la cea simpla, adaugand inca 2 straturi convolutionale,
        am adaugat un strat dupa al doilea strat din arhitectura initiala, si inca unu dupa al doilea MaxPooling2D.

        Ultimele 2 se bazeaza tot pe strutura initiala, doar ca am modificat numarul de filtre,
        a 3-a are 265 in loc de 128 de filtre pe al doilea strat convolutional, si 384 in loc de 256 de filtre pe al 3-lea
        strat convolutional.

        A 4-a structura are la toate straturile adaugate 128 de filtre in plus fata de numarul de filtre initial.
        Toate struturile retelei sunt salvate intr-un json al fiecarei retele.

    * Antrenarea retelei (train.py)
        Am folosit metota fit_generator, pentru a antrena reteaua direct folosind generatorul obtinut inainte, si am folosit
        callback-uri pentru salvarea celui mai bun model (ModelCheckpointer) si pentru a salva informatiile obtinute pe durata
        antrenarii (Tensorboard), unde se pot vizualiza mai bine graficele.
        Toate ponderile sunt salvate intr-un fisier pentru fiecare model in parte.

    * Evaluarea retelei (evaluate.py)
        Pentru evaluare am incarcat modelul cu ponderile acestuia si am folosit metoda predict_generator, pentru a
        obtine labelurile prezise, apoi pentru a stabili acuratetea per imagine, am folosit din sklearn, accuracy_score,
        care calculeaza acuratetea simplu, iar pentru acuratetea per studiu, am folosit un dictionar pentru fiecare pacient si
        studiile acestuia, si am adunat +1 pentru pozitiv si -1 pentru negativ, iar pentru a stabili clasa studiului, daca suma are >0, clasa
        prezisa este pozitiva, daca este <0 este negativa, iar daca suma este 0, se ia random o clasa din cele 2.
    
    * Rezultate obtinute
        Pentru arhitectura din enunt:
        10 epoci
        Acuratetea per studiu 64,78% (test)
        Acuratetea per imagine 63,43% (test)
        Acuratetea per studiu 70,54% (train)
        Acuratetea per imagine 68,93% (train)

        20 epoci
        Acuratetea per studiu 70,23% (test)
        Acuratetea per imagine 68,25% (test)
        Acuratetea per studiu 78,86% (train)
        Acuratetea per imagine 75,58% (train)

        Pentru arhitectura a2a (multi layer):
        10 epoci
        Acuratetea per studiu 65,37% (test)
        Acuratetea per imagine 63,8% (test)
        Acuratetea per studiu 68,92% (train)
        Acuratetea per imagine 66,79% (train)

        20 epoci
        Acuratetea per studiu 67,65% (test)
        Acuratetea per imagine 66,68% (test)
        Acuratetea per studiu 76,49% (train)
        Acuratetea per imagine 74,44% (train)

        Pentru arhitectura a3a (multi feature maps):
        10 epoci
        Acuratetea per studiu 67,65% (test)
        Acuratetea per imagine 65,93% (test)
        Acuratetea per studiu 72,30% (train)
        Acuratetea per imagine 69,51% (train)

        20 epoci
        Acuratetea per studiu 68,45% (test)
        Acuratetea per imagine 66,59% (test)
        Acuratetea per studiu 75,33% (train)
        Acuratetea per imagine 73,41% (train)

        Pentru arhitectura a4a (multi feature maps v2):
        20 epoci
        Acuratetea per studiu 70,23% (test)
        Acuratetea per imagine 67,59% (test)
        Acuratetea per studiu 77,66% (train)
        Acuratetea per imagine 75,14% (train)

    * Concluzii
        Toate arhitecturile ajung la fenomenul de overfitting, daca privim graficile de train loss vs valid loss,
        observam ca train loss-ul scade mereu, iar val loss-ul o sa creasca la un moment dat, de aceea nu am lasat mai
        mult de 20 epoci, o sa se ajunga sa scada mereu train loss-ul, ajungand la o acuratete mare, dar cea de validare
        o sa scada gradual.

        Cea mai buna arhitectura a fost cea initiala, (68,25% per imagine, 70,23% per studiu).
        
