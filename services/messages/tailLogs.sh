for i in $( docker ps -q )
do  
  docker logs -f $i &  
done 
