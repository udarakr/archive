<?php
$memcache = new Memcache();
$memcache->addServer('localhost', 11211) or die ("Could not connect");

$key = md5('my-name'); // Unique key
$cache_result = array();
$cache_result = $memcache->get($key); // Memcached object 

if($cache_result){
	// Second  Request
	$demos_result=$cache_result;
	echo "Result found in memcache\n";
}else{
	// Initial Request
	$name= "Udara R";
	$memcache->set($key, $name); 
	echo "Result not found in memcache, added to the memcache \n";
}

echo $cache_result."\n";
?>
