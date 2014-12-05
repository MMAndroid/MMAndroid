MMUnB
===========================

Esse projeto tem como objetivo servir como base 
para o ensino do desenvolvimento na plataforma Android.
Consiste em uma nova versao do Mobile Media, 
player multimidia para aplicacoes moveis e bem 
disseminado como estudo de caso para avaliar 
implementacoes de Linhas de Produtos de Software. 
As versoes anteriores eram para a plataforma Java 
Micro Edition.


Features dessa versao
===========================
* As imagens que são exibidas em ListView estão sendo adicionadas em cache.
* Paginção dos resultados das consultas no DB para popular ListView.

Player basico para execucao de videos armazenados em todo dispositiovo.
Em mais detalhes, cobre:

* Mudanca de activities (Telas) (Registro dessas atividades no manifest)

* Passagem de parametros entre telas

* Listagem de arquivos dentro do SDCard (Pasta "/DCIM/Camera/" com extensão .pm4)

* Iniciar, pausar e parar uma exibição de vídeo. 

Proximas releases
===========================

* Suportar outras midias alem de videos. 

* Poder atribuir informacoes as midias armazenadas, como nocoes de 
playlist, preferencias sobre musicas, videos e fotos, atribuir informacao 
de localizacao (essa musica me lembra a Chapada dos Viadeiros, por exemplo), 
relacionar midias, como essa musica esta associada a um evento especifico, e 
tais fotos foram tiradas nesse evento, etc.


* Executar midias que estao em um servidor remoto

* Load Datas in background with interface LoaderManager.
* Implementar a possibilidade do usuário listar em quais diretórios seja pesquisado as medias.
* Implementar a possibilidade de multi seleção para listview de medias e adicionar diretamente em uma plylist existente.



Para colaboradores
===========================

* Veja diagramas UML em /docs
