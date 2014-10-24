<?php
//$str = 'udara kasun rathnayake';

$len = strlen($str);
echo $len."\n"; // return 0 for Undefined $str
#if we need to do a validation isset is more suitable
if (isset($str[21])){ // return false for Undefined variable
	echo "true\n";
}else{
	echo "false\n";
}

?>
