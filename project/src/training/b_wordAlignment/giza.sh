










en_name=europarl.en.devel
es_name=europarl.es.devel

en_src=../../../data/${en_name}
es_src=../../../data/${es_name}

~/giza-pp-master/GIZA++-v2/plain2snt.out $en_src $es_src

~/giza-pp-master/mkcls-v2/mkcls -p${en_src} -V${en_src}.vcb.classes
~/giza-pp-master/mkcls-v2/mkcls -p${es_src} -V${es_src}.vcb.classes

~/giza-pp-master/GIZA++-v2/GIZA++ -S ${es_src}.vcb -T ${en_src}.vcb -C ${es_src}_${en_name}.snt -o EnToEs -outputpath ../../../data/
