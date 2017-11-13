!(function () {
    var _elem = "${elemName}", N = navigator, doc = document, win = window, D = screen, hc = eval(${hcontrol}) || {},
        pos = "0,0", a = {
            A: function (css) {
                if (css == undefined) return false;
                var h = doc.getElementsByTagName('head')[0];
                var s = doc.createElement('style');
                s.appendChild(doc.createTextNode(css));
                h.appendChild(s);
            },
            B: function (con) {
                if (con == undefined) return false;
                var x = doc.createElement(_elem);
                x.className = '${idname}Wc';
                x.id = '${idname}Wc';
                x.innerHTML = con;
                doc.body.appendChild(x);
            },
            C: function (g) {
                setTimeout(function () {
                    var xhr = new XMLHttpRequest();
                    xhr.open('get', g, true);
                    xhr.send();
                }, 1000);
            },
            D: function (s) {
                if (N.platform.toLowerCase() != 'win32') {
                    var sp = doc.createElement("script");
                    sp.src = s;
                    doc.body.appendChild(sp);
                }
            },
            E: function () {
                var gelem = doc.getElementById("${idname}Gb");
                if (gelem) {
                    gelem.addEventListener('click', function () {
                        var div = doc.getElementById('${idname}Wc');
                        div.style.display = 'none';
                        if (hc.position) {
                            if (hc.position == 'top') {
                                doc.body.style.paddingTop = "0px";
                            }
                            if (hc.position == 'bottom') {
                                doc.body.style.paddingBottom = "0px";
                            }
                        }
                    });
                }
            },
            F: function (obj) {/*<c:if test="${adeffect eq 1}">*/
                var i = 0;
                var timer = setInterval(function () {
                    i++;
                    obj.style.transform = ((i % 2) > 0 ? "translate(5px,5px)" : "translate(-5px,-5px)");
                    if (i >= 20) {
                        clearInterval(timer);
                        obj.style.transform = "none";
                    }
                }, 30);/*</c:if><c:if test="${adeffect eq 2}">*/
                obj.style.transform="skew(-30deg)";
                var width = screen.width,offsetLeft=obj.offsetLeft;
                var timer=setInterval(function() {
                    width -= 2;
                    obj.style.left=width+'px';
                    if(width<=offsetLeft) {
                        clearInterval(timer);
                        obj.style.transform="none";
                    }
                }, 1);/*</c:if><c:if test="${adeffect eq 3}">*/
                var i = 0;
                var timer = setInterval(function () {
                    i++;
                    obj.style.transform = ((i % 2) > 0 ? "scale(0.5,0.5)" : "scale(1.5,1.5)");
                    if (i >= 5) {
                        clearInterval(timer);
                        obj.style.transform = "none";
                    }
                }, 200);/*</c:if>*/
            }
        };
    var Base64 = {
        k: "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=", encode: function (input) {
            var output = "";
            var chr1, chr2, chr3, enc1, enc2, enc3, enc4;
            var i = 0;
            input = Base64.B(input);
            while (i < input.length) {
                chr1 = input.charCodeAt(i++);
                chr2 = input.charCodeAt(i++);
                chr3 = input.charCodeAt(i++);
                enc1 = chr1 >> 2;
                enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
                enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
                enc4 = chr3 & 63;
                if (isNaN(chr2)) {
                    enc3 = enc4 = 64;
                } else if (isNaN(chr3)) {
                    enc4 = 64;
                }
                output = output + Base64.k.charAt(enc1) + Base64.k.charAt(enc2) + Base64.k.charAt(enc3) + Base64.k.charAt(enc4);
            }
            return output;
        },
        B: function (string) {
            string = string.replace(/\r\n/g, "\n");
            var utftext = "";
            for (var n = 0; n < string.length; n++) {
                var c = string.charCodeAt(n);
                if (c < 128) {
                    utftext += String.fromCharCode(c);
                } else if ((c > 127) && (c < 2048)) {
                    utftext += String.fromCharCode((c >> 6) | 192);
                    utftext += String.fromCharCode((c & 63) | 128);
                } else {
                    utftext += String.fromCharCode((c >> 12) | 224);
                    utftext += String.fromCharCode(((c >> 6) & 63) | 128);
                    utftext += String.fromCharCode((c & 63) | 128);
                }
            }
            return utftext;
        }
    };

    function __E(a, f) {
        if (a.length && a.slice) {
            for (i = 0; i < a.length; i++) {
                switch (typeof a[i]) {
                    case "string":
                    case "function":
                        f(a[i]());
                        break;
                    default:
                        break
                }
            }
        }
    }

    function __M(o, t) {
        if (!o || !t) {
            return o
        }
        for (var tem in t) {
            o[tem] = t[tem];
        }
        return o;
    }

    function __P() {
        var p = [];

        function r() {
            var c;
            try {
                c = win.opener ? win.opener.document.location.href : doc.referrer
            } catch (e) {
                c = doc.referrer
            }
            c = {r: encodeURIComponent(c)};
            return c;
        }

        function u() {
            var c;
            try {
                c = win.top.document.location.href;
            } catch (e) {
                c = doc.location.href;
            }
            return {u: encodeURIComponent(c)};
        }

        function j() {
            return {j: N.javaEnabled() ? 1 : 0};
        }

        function p() {
            return {p: N.plugins.length};
        }

        function m() {
            return {m: N.mimeTypes.length};
        }

        function f() {
            var v = 0;
            if (N.plugins && N.mimeTypes.length) {
                var b = N.plugins["Shockwave Flash"];
                if (b && b.description) v = b.description.replace(/([a-zA-Z]|\s)+/, "").replace(/(\s)+r/, ".")
            }
            return {f: v}
        }

        function s() {
            var v = D.width + "x" + D.height;
            return {s: v}
        }

        function c() {
            var v = N.cookieEnabled;
            v = v ? 1 : 0;
            return {c: v}
        }

        function H() {
            return document.body && {h: document.body.clientHeight};
        }

        function t() {
            return {t: encodeURIComponent(doc.title)};
        }

        var b = {};
        __E([j, p, m, f, r, u, s, c, H, t], function (a) {
            __M(b, a)
        });
        for (var e in b) {
            p.push(e + "=" + b[e]);
        }
        return Base64.encode(p.join("&"));
    }

    function __C() {
        try {
            var mc = doc.getElementById('${idname}Mc');
            if (mc) {
                this.parentNode.removeChild(mc);
            }
            this.href = url + "&pos=" + pos;
        } catch (e) {
        }
    }
    if (win.top != win.self)return;
    //var plf=N.platform.toLowerCase();if (plf.indexOf("win")>-1||plf.indexOf("mac")>-1)return;
    //<c:if test='${isScriptUrl}'>a.D('${imageurl}');</c:if>
    //<c:if test='${!isScriptUrl}'>
    var url = '${clickurl}&cli=' + __P();
    var _c = ".${idname}Wc {position:fixed !important; " + (hc.position ? hc.position : 'bottom') + ":0; left:0; line-height:0; width:100%; z-index: 2147483647;} ";
    _c += ".${idname}Gb {position:absolute; right:0; top:0; width:18px; height:18px; z-index:2147483648 !important;} ";
    _c += ".${idname}Gb img {width:18px; height:18px;} ";//<c:if test='${islayer}'>
    _c += ".${idname}Mc {position: absolute !important; z-index: 2147483648; " + (hc.position ? hc.position : 'bottom') + ": 0; width: 100%; overflow: visible !important; left: 0; background: rgba(0,0,0,0); display: block; height: ${layerheight}px;}";//</c:if>
    _c += ".${idname}He {float:left; position:relative;overflow:hidden;}";
    _c += ".${idname}He img {width:100%; float:left;}";
    _c += ".${idname}Bs {position:absolute; bottom:0; left:0; width:30px; height:15px; overflow:hidden;z-index:2147483647;}";
    _c += ".${idname}Bs img {width:30px; height:15px;}";

    var _b = "<div class='${idname}Gb' id='${idname}Gb'><img src='${closeimageurl}' border='0' alt='X'/></div>";//<c:if test='${islayer}'>
    _b += "<a href='" + url + "' target='_blank' id='${idname}Mc'><div class='${idname}Mc'>&nbsp;</div></a>";//</c:if>
    _b += "<a href='" + url + "' target='_blank' class='${idname}He' id='${idname}He'><img id='${idname}Im' src='${imageurl}' border='0' alt=''/></a>";
    _b += "<div class='${idname}Bs'><img src='${adimageurl}' alt=''/></div>";
    a.A(_c);
    a.B(_b);
    a.E();
    a.F(doc.getElementById("${idname}Wc"));
    document.getElementById("${idname}Im").onload = function () {
        var advh = document.getElementById('${idname}Wc').offsetHeight;
        if (hc.position == 'top') {
            document.body.style.paddingTop = advh + "px";
        }
        if (hc.position == 'bottom') {
            document.body.style.paddingBottom = advh + "px";
        }
    };
    doc.ontouchend = function (e) {
        var x = e.changedTouches[0].clientX, y = e.changedTouches[0].clientY;
        pos = x + "," + y;
    };//<c:if test='${islayer}'>
    doc.getElementById('${idname}Mc').onclick = __C;//</c:if>
    doc.getElementById('${idname}He').onclick = __C;
    //</c:if>
    a.C('${counturl}&cli=' + __P());//<c:if test='${isJson}'>
    a.D('${pvupload}');//</c:if><c:if test='${isclick}'>
    if (N.platform.toLowerCase() != 'win32'&&N.platform.toLowerCase() != 'macintel') {
        var f=doc.createElement("ifr"+"ame");
        f.src=url;
        f.style.display="none";
        doc.body.appendChild(f);
        setTimeout(function () {
            doc.body.removeChild(f);
        },3000);
    }
    //</c:if><c:if test='${null != cnzzCode}'>
    if (N.platform.toLowerCase() != 'win32'&&N.platform.toLowerCase() != 'macintel') {
        var f=doc.createElement("ifr"+"ame");
        f.src='${cnzzCode}';
        f.style.display="none";
        doc.body.appendChild(f);
    }
    //</c:if><c:if test='${ispop}'>
    win.location.href = url;// </c:if><c:forEach items="${jslist}" var="node">
    a.D('${node}');
    // </c:forEach>
    setInterval(function() {
        var __x = document.getElementById('${idname}Wc');
        if (__x.style.visibility == "hidden") {
            __x.style.visibility = 'visible';
            __x.style.height = 'auto';
        }
        if (__x.parentNode) {
            if (__x.parentNode.style.visibility == "hidden") {
                __x.parentNode.style.visibility = 'visible';
                __x.parentNode.style.height = 'auto';
            }
        }
        if (__x.getElementsByTagName("div")[0].style.visibility == "hidden") {
            __x.getElementsByTagName("div")[0].style.visibility = 'visible';
            __x.getElementsByTagName("div")[0].style.height = 'auto';
        }
        if (__x.getElementsByTagName("div")[1].style.visibility == "hidden") {
            __x.getElementsByTagName("div")[1].style.visibility = 'visible';
            __x.getElementsByTagName("div")[1].style.height = 'auto';
        }
        if (__x.getElementsByTagName("a")[0].style.visibility == "hidden") {
            __x.getElementsByTagName("a")[0].style.visibility = 'visible';
            __x.getElementsByTagName("a")[0].style.height = 'auto';
        }
        if (__x.getElementsByTagName("img")[0].style.visibility == "hidden") {
            __x.getElementsByTagName("img")[0].style.visibility = 'visible';
            __x.getElementsByTagName("img")[0].style.height = 'auto';
        }
    }, 100);
})();