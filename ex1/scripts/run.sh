#!/bin/bash  +x
en=en_text.corp
es=es_text.corp
ca=ca_text.corp

en_t=en.test
es_t=es.test
ca_t=ca.test



for a in 2 3 4 
do
	echo wb for english n=$a
	./lm -i $en  -o model${en}  -n $a -s wb
	./eval -i $en_t -m model${en}
	echo wb for espaniol n=$a
	./lm -i $es  -o model${es}  -n $a -s wb
	./eval -i $es_t -m model${es}
	echo wb for catalan n=$a
	./lm -i $ca  -o model${ca}  -n $a -s wb
	./eval -i $ca_t -m model${ca}
done
