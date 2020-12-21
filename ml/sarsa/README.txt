Mihaila Alin-Florin
343C1

Rezultate teste:

== Epsilon Greedy vs Softmax ==
Am testat ambele metode pe hartile empty6x6 si empty8x8 si de fiecare data Epsilon Greedy a invatat mai repede decat
Softmax. Pentru hartile empty6x6 si empty8x8 ambele metode converg in 150-200 episoade. Pentru harta empty16x16 nu a reusit
nicio metoda sa convearga, am lasat pana la 50k episoade, iar castigul mediu ramanea la 0.

== Valori hiperparametrii ==

Epsilon Greedy:
    Prima data am variat constanta (C), pornind de la 0.05 pana la 0.6, si am observat ca daca se mareste constanta, castigul mediu creste
    mai repede, datorita faptului ca marim sansa de a explorare. (se poate vedea in graficele egreedy_var_c_empty6x6 si egreedy_var_c_empty8x8)

    La varierea ratei de invatare am observat ca la cresterea parametrului, obtin rezultate din ce in ce mai slabe, am variat rata de invatare intre 0.1 si 0.4.
    (din graficele egreedy_var_lr_empty6x6 si egreedy_var_lr_empty8x8 se poate observa aceasta variatie)

    Pentru a testa si initializarea optimista (q0 > 0), am observat ca algoritmul converge mai greu fata de initializarea normala cu 0, deoarece o sa ajunga sa exploreze
    mai mult fata de varianta initiala, si o sa ajunga sa micsoreze valorile de la q0 pana la 0, de aceea o sa ii ia mai mult sa convearga. 

Softmax:
    Pentru metoda softmax, am variat doar rata de invatare si am obtinut rezultate asemanatoare ca la Epsilon Greedy.
    Initializarea optimista nu a mers aici, deoarece beta lua valori foarte mari (numitorul avea la inceput valori de dimensiunea 10^(-4)) si ajungea
    ca exponentul sa fie foarte mare si primeam overflow exception.

== Hartile DoorKey ==
Pentru hartile cu cheie si usa, am intampinat probleme deoarece algortimul nu converge atunci cand harta se schimba, am realizat un grafic si se poate
observa ca depinde de ce castig mediu are la inceput, daca are de exemplu 0.3, castigul mediu in continuare o sa se invarta in jurul acestei valori, cu mici
variatii.

Am facut testari si pe harti cu seed-ul fixat, si aici agentul a reusit sa invete destul de repede, aproximativ la fel ca pe hartile fara cheie si usa.
(se poate vedea in directorul doorkey, grafice pentru random seed si seed fixat)

