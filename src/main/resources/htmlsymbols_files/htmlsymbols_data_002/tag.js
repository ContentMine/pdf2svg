




var myRand=parseInt(Math.random()*99999999);

var pUrl = "http://lifenet.sitescout.com/disp?pid=FFB2103&rw=1&rand=" + myRand;

var strCreative=''
 + '<IFRAME SRC="'
 + pUrl
 + '" WIDTH="300" HEIGHT="250" MARGINWIDTH=0 MARGINHEIGHT=0 HSPACE=0 VSPACE=0 FRAMEBORDER=0 SCROLLING=no BORDERCOLOR="#000000">\n'
 + '</IFRAME>\n'
 + '\n'
;
document.write(strCreative);