var switch_advert_stop = 0;

function baseJS() {
    var self = this;
    this.dcount = 0;
    this.dct = 0;
    this.d1 = 0;
    this.dd = 0;
    this.fre = 2;
    this.n_max = 120;
    this.stime = new Date().getTime();
    this.__cuf = '';
    this.stayexe = function (url, n) {
        if (url.indexOf("?") == -1) {
            url += "?"
        } else {
            url += "&"
        }
        url += "t=" + new Date().getTime() + "&n=" + n;
        var img = new Image();
        img.onload = img.onerror = function () {
        };
        img.src = url
    };
    this.ts = function (event) {
        self.dd = 0;
        self.d1 = event.targetTouches[0].pageY;
        self.dctDo()
    };
    this.dctDo = function () {
        window.comxydct++
    };
    this.dcountDo = function () {
        window.comxydcount++
    };
    this.tm = function (event) {
        self.dd = event.targetTouches[0].pageY - self.d1
    };
    this.te = function (event) {
        if (self.dd > 0 || self.dd < 0) {
            self.dcountDo()
        }
    };
    this.action1 = function (url) {
        if (typeof window.new17unaction1 != 'undefined') {
            return
        }
        window.comxydcount = 0;
        window.comxydct = 0;
        window.new17unaction1 = 1;
        if (document.addEventListener) {
            document.addEventListener('touchstart', self.ts, false);
            document.addEventListener('touchmove', self.tm, false);
            document.addEventListener('touchend', self.te, false)
        }
    };
    this.stay = function (url) {
        if (!switch_advert_stop) {
            return
        }
        if (typeof window.new17untime != 'undefined') {
            return
        }
        window.new17untime = 1;
        self.stayexe(url, 1);
        var timer = setInterval(function () {
            var n = parseInt((new Date().getTime() - self.stime) / 1000, 10);
            if (n > self.n_max && timer) {
                clearInterval(timer);
                return
            }
            if (n > 5 && n <= 10) {
                self.fre = 5
            } else if (n > 10) {
                self.fre = 10
            }
            if (n % self.fre != 0) {
                return
            }
            self.stayexe(url, n)
        }, 1000)
    };
    this.xy = function (o, e, id) {
        var x = e.offsetX || e.layerX;
        var y = e.offsetY || e.layerY;
        var place_width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        var place_height = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
        var screen_width = window.screen.width.toString();
        var screen_height = window.screen.height.toString();
        var w = 0;
        var h = 0;
        if (id != null) {
            w = document.getElementById(id).width;
            h = document.getElementById(id).height
        }
        if (o.getElementsByTagName("a")) {
            var obj = o.getElementsByTagName("a");
            if (obj[0].href.indexOf("&") > -1) {
                obj[0].href = obj[0].href.substring(0, obj[0].href.indexOf("&"))
            }
            var a = [];
            a.push("x=" + x);
            a.push("y=" + y);
            a.push("w=" + w);
            a.push("h=" + h);
            a.push("place_width=" + place_width);
            a.push("place_height=" + place_height);
            a.push("screen_width=" + screen_width);
            a.push("screen_height=" + screen_height);
            a.push("dcount=" + window.comxydcount);
            a.push("dct=" + window.comxydct);
            var cci = [];
            cci.push("a=" + navigator["platform"]);
            cci.push("b=" + navigator["cookieEnabled"]);
            a.push("cci=" + encodeURIComponent(cci.join("&")));
            a.push("click_url_final=" + encodeURIComponent(this.__cuf));
            obj[0].href = obj[0].href + '&' + a.join("&")
        }
    };
    this.cci = function () {
        var r = [];
        r.push("a=" + navigator["platform"]);
        r.push("b=" + navigator["cookieEnabled"]);
        return encodeURIComponent(r.join("&"))
    }
}

var baseJS = new baseJS();
baseJS.stay("http://m.uerzyr.cn/z3-2.html?s=eNoVx0EOhCAMXDDAv3jgjCA0dcNbSGkx4SDoWnz_Zuc2z45xX4g18ZhdM8lbv5ofHZdhbWdNa7AxIKwRjIyTWs9NUgTvzeztnvVfZE8VXSjFObuRpwJ4sBMJBPaAbfn8XDAdoCEb");
baseJS.action1();
;(function () {
    var SRxz = {}, nu71t_c4ca4238a0b923820dcc509a6f75849b = "nu71t_c4ca4238a0b923820dcc509a6f75849b";
    if (window[nu71t_c4ca4238a0b923820dcc509a6f75849b] != undefined) {
        return;
    }
    ;var stop_time = "";
    var doc = document;
    var ref;
    try {
        ref = top.document.referrer || document.referrer;
    } catch (err) {
        ref = document.referrer;
    }
    var url;
    try {
        url = location.href;
        if (self != top) {
            ref = top.location.href || document.referrer;
        }
    } catch (e) {
        url = location.href;
    }
    SRxz.is_group_pv = 0;
    if (window['_UN_GROUPPVUNION_'] == undefined) {
        window['_UN_GROUPPVUNION_'] = 0;
        SRxz.is_group_pv = 1;
    }
    ;SRxz.init = {
        ClickTarget: 5,
        OpenMethod: "_blank",
        VUrl: 'http://m.uerzyr.cn/stat-z191247.html?Hxu=eNpNkMuOwyAMRf-lC5YVjxBCR3wLcozboiYhQ0iq_v2QNjOajcU1x9eP5dLI9nICLA5hGHrAB8MSR3JC81ZbI1rDlhmQfAxOGK0sC2mEOO26NUoxCBvlsksprDTsmul7pQlf7vgJ8PKCYSYocSO_rLHUOMD28VSy6QSbc0Ry8qw5i1NYl5JfHqHQLdVHxWp69m_npTgh1Lkz5xqF7dg6xZp23FqUXdtZQdB0CrgQVqMyVmJLDW8ObjezqICs1H0vJW9AQW_sFWUIGgy_moZhSo9IfoSC99q6z-m5UK6lfyO0gqWZph2hHGGo1FsfaJVj6uNA_rjW_120YivWdfaCCi7PWNv4kUIE_7mD-L0q3iHfyJc0D3GMxfHT1w9arpO-&is_group_pv=' + SRxz.is_group_pv + '&screen_width=' + window.screen.width.toString() + '&screen_height=' + window.screen.height.toString() + '&ref=' + encodeURIComponent(ref) + '&url=' + encodeURIComponent(url) + '&cci=' + baseJS.cci(),
        Images: "http://mpic.jmgehn.cn/tr-2/2017/09/0/1505792211517.gif",
        HomeUrl: "http://www.17un.com",
    };
    SRxz.cm = "nu71t_15065971670629";
    SRxz.Cok = 0;
    SRxz.$ = function (e) {
        if (typeof e == "string") {
            return doc.getElementById(e);
        }
        return false;
    };
    SRxz.is_deviant = function () {
        var v = doc.querySelector("meta[name=viewport]");
        var h = SRxz.$(SRxz.cm + "Fi").height;
        var max = 240 + (!v ? 70 : 0);
        if (h > max || h < 50) {
            return true;
        }
        return false;
    };
    SRxz.hide = function (o) {
        try {
            SRxz.$(o).style.display = 'none';
            SRxz.Cok = 1;
        } catch (e) {
        }
    };
    SRxz.SC = function (css) {
        if (css == null) {
            return;
        }
        try {
            var head = doc.getElementsByTagName("head")[0];
            var style = doc.createElement("style");
            style.type = "text/css";
            if (style.styleSheet) {
                style.styleSheet.cssText = css;
            } else {
                style.appendChild(doc.createTextNode(css));
            }
            head.appendChild(style);
            return true;
        } catch (e) {
            return false;
        }
    };
    SRxz.IS = function (innerHTML) {
        var x = doc.createElement("div");
        x.className = nu71t_c4ca4238a0b923820dcc509a6f75849b;
        x.innerHTML = innerHTML;
        try {
            if (doc.body) {
                doc.body.insertBefore(x, doc.body.firstChild);
            } else {
                doc.getElementsByTagName('html')[0].appendChild(x);
            }
            return true;
        } catch (e) {
            return false;
        }
    };
    SRxz.OP = function (innerHTML) {
        if (innerHTML == null) {
            return false;
        }
        var x = doc.createElement("div");
        x.className = nu71t_c4ca4238a0b923820dcc509a6f75849b;
        x.id = nu71t_c4ca4238a0b923820dcc509a6f75849b;
        x.innerHTML = innerHTML;
        if (doc.body) {
            try {
                doc.body.appendChild(x);
            } catch (e) {
            }
        } else {
            try {
                doc.getElementsByTagName("html")[0].appendChild(x);
            } catch (e) {
            }
        }
        return true;
    };
    SRxz.reset = function (f, e) {
        if (e == null) {
            e = "onresize";
        }
        self.setInterval(function () {
            try {
                if (SRxz.$(nu71t_c4ca4238a0b923820dcc509a6f75849b)) {
                    f();
                    doc.body.appendChild(SRxz.$(nu71t_c4ca4238a0b923820dcc509a6f75849b));
                }
            } catch (e) {
            }
        }, 1000);
        var oe = window[e];
        if (typeof window[e] != "function") {
            window[e] = f;
        } else {
            window[e] = function () {
                oe();
                f();
            }
        }
    };
    SRxz.click = function () {
        if (SRxz.init.ClickTarget) {
            SRxz.init.ClickTarget = 0;
            SRxz.$(SRxz.cm + "Cc").click();
        } else {
            SRxz.hide(nu71t_c4ca4238a0b923820dcc509a6f75849b);
            SRxz.hide(SRxz.cm + "Dx");
            clearInterval(stop_time);
        }
    };
    SRxz.XY = function () {
        var zZz = new Image();
        zZz.src = "http://cks.gongyituan.cn/17un/service.php?token8=eNo1jMsOgyAQRf_FBWuZkQ5j47eQ4WFCmqItatK_LzTp8tzHqbNGOw8SjiWXmt6HC9v2yKmquktILsdFk0FWcXtKLp1vhKgkXn3cEDQDqbN9O6G1Wu3ycf-g6bnVJb_On40DSmIw3gOMk6B44jVAjEZoXFxpGu5fXCd4L1U,";
    };
    SRxz.pv = function () {
        var zZ_a = new Image();
        zZ_a.src = SRxz.init.VUrl;
    };
    SRxz.pvMonitor = function () {
        var x = doc.createElement('div');
        x.innerHTML = "<iframe src='http://m.uerzyr.cn/stat-xGU5.html?ts=eNodysEOgyAMXDDQf_HAmRahqwvfYkrBhIPoJvj9izu-5F0LwGuZRHvUY7S-nrfRXvcSwdvgmSCQuU7RstYcgbxjk49danscyDkj-S7f_hCBkcxo9TP-ndVJYfQpIdpZnCTiTTFnL2Q3mqf3DwMuXCc_' height='0' width='0' style='display:none'></iframe>";
        var s = SRxz.$("pvnu71t_c4ca4238a0b923820dcc509a6f75849b");
        try {
            s.appendChild(x);
            return !1;
        } catch (e) {
        }
    };
    SRxz.tjs = function () {
        return;
        var script = document.createElement('script');
        script.setAttribute('src', 'http://js.saiqizhi.com/js/qc.js?advert=61');
        script.async = true;
        document.body.appendChild(script);
    };
    SRxz.tjs();
    var _html = "<div id='" + SRxz.cm + "Dx'></div>";
    _html += "<iframe src='" + SRxz.init.VUrl + "' height='0' width='0' style='display:none'></iframe>";
    _html += "<div id='pvnu71t_c4ca4238a0b923820dcc509a6f75849b' style='display:none'></div>";
    var html = "";
    html += "<a id='" + SRxz.cm + "Cc' href='http://m.uerzyr.cn/stat-qq.html?Hxu=eNpFUMtupDAQ_JdI8XHkx4AhEYe95Dcs024GK4C9tpkRf5_mkV0JGVd1u6q680ct7h9vFkoHk4dvBsXP2ImK11WrRa1ZjhbQeNcJXamWuTBbv-y41kox656Yyg6laKVmc-j9hGbNmHZSNY1ga5rMId6NpcR39eddftFnY7zBaJfHZkNd3yDMRG5hlfRjVEgPNGWLp_ev0X9iSPh3xQW27io5uxnBIKEt_okmr77QOdnn-UDJO2WJyQN28lZx5he35pI2A7bgI9CF2jiLJPMbX6im1SxO-wZe3pWxU5JfeET_GEtcJ4g4SoaMp6N-Vi5MRuviKWmnWseHWiFwvGPTOqE5CrBNxXuA2nJ27MjksCaKCFPIyHw0x5iZjIS6NfpGp2ibS3PP2IKy2Mqq76Xkd6tsr9sBpHOV1XzQd5bLNuE5GwyWNndImxiyLz4sXdrTMgjh26OZbYGROvsUXucS_gWoBQsRl70Fk6fZ-ImvVoIrUN6do3t-eVIyMzpvzbl18fb5AwUS1T0,' style='display:none'></a>";
    html += "<a id='" + SRxz.cm + "Fx' class='" + SRxz.cm + " " + SRxz.cm + "Fx' href='#' onclick='" + nu71t_c4ca4238a0b923820dcc509a6f75849b + ".hide(\"" + SRxz.cm + "Fx\");" + nu71t_c4ca4238a0b923820dcc509a6f75849b + ".$(\"" + SRxz.cm + "Cc\").click();" + nu71t_c4ca4238a0b923820dcc509a6f75849b + ".XY();return false'></a>";
    html += '<div id="' + SRxz.cm + '" class="' + SRxz.cm + '" onclick="baseJS.xy(this, event, \'' + SRxz.cm + 'Fi\');" data-s="17un">';
    html += "<a id='" + SRxz.cm + "Fa' class ='" + SRxz.cm + "Fb " + SRxz.cm + "Fa' href='http://m.uerzyr.cn/stat-qq.html?Hxu=eNpFUNtuhSAQ_Jcm5dFwOYq24aEv_Q2CsB5JVSjgOfHvu17aJgaZmWVndvNbQ7u3F2OLspO3X8QWP4NiNW3qTrJGkhyNBe2dYrIWHXFhNn7ZcSOFIMY9IJUdctZxSebQ-wn0miHtpGhbRtY06aO5GkuJr-LjlX_iZ2Ks7GiW-2ZC01Q2zEhuYeX4IyikO-iyxdP71-ifGBJ8r7DYTV2SM5tmxCYwxT9A59UXPFwn8zgfCH7DLDF5C4pXNSV-cWsuadPWFLgHvGAZJRHb_MZnou0kidO-gad3ZVSC0wuP4O9jUQyJQ9JoPB36qVxcGI3WxWNSRXtoW1ED9LJuGAwgJGN123Lh-FwwVJJjRzqHNWFE4qM-JszowUTVygpP1rVXuz1eZ4WBjtd9zzm9GWF62Q2WO1cbSQd5I7lsE5xj2cHg0uwUMugYsi8-LCrtQYkN4cuDnk2xI1b2KTzP-f8CNIyECMteAsnjWPTEVynC1WLencN7fnrspGdw3uhz4ezl_QcwpdKv' target='" + SRxz.init.OpenMethod + "' onclick='" + nu71t_c4ca4238a0b923820dcc509a6f75849b + ".XY();'>";
    html += "<img id='" + SRxz.cm + "Fi' src='" + SRxz.init.Images + "' width='1'></a>";
    html += "<div class='" + SRxz.cm + "CR'><a href='javascript:void(0)'><img src='http://mpic.jmgehn.cn/union/17un_logo_vss1.png'></a></div>";
    html += "<div class='" + SRxz.cm + "Ca'><img class='" + SRxz.cm + "Cb' src='http://mpic.jmgehn.cn/union/17un_close_s1.png' onclick='" + nu71t_c4ca4238a0b923820dcc509a6f75849b + ".click();'></div>";
    html += "</div>";
    var css = '.' + SRxz.cm + '{position:fixed;z-index:2147483644;top:0px;width:100%;overflow:visible !important;height:0;}';
    css += '.' + nu71t_c4ca4238a0b923820dcc509a6f75849b + ',.' + nu71t_c4ca4238a0b923820dcc509a6f75849b + ' *{margin:0;padding:0;border:0;min-width:none;max-width:none;}';
    css += '#' + SRxz.cm + 'Dx{position:relative;z-index:2147483644;top:0px; width:100%; overflow:visible !important;height:0;display:block;}';
    css += '.' + SRxz.cm + 'Fa{width:100%;float:left;text-align:center;background-size:100% auto !important;position:relative;}';
    css += '.' + SRxz.cm + 'Fb{background:url(' + SRxz.init.Images + ') rgba(0,0,0,0) no-repeat center;}';
    css += '.' + SRxz.cm + 'Fb:hover{background:url(' + SRxz.init.Images + ') rgba(0,0,0,0) no-repeat center;}';
    css += '.' + SRxz.cm + 'Ca{position:absolute;right:0;bottom:0px}';
    css += '#' + SRxz.cm + 'Fx{ background:rgba(0,0,0,0);}';
    css += '#' + SRxz.cm + 'Fi{width:100%;float:left;visibility:hidden;}';
    css += '.' + SRxz.cm + 'Cb{float:right;width:auto !important;}';
    css += '.' + SRxz.cm + 'CR{bottom:0;position:absolute;left:0;}.' + SRxz.cm + 'CR a{float:left;}.' + SRxz.cm + 'CR img{float:left;}';
    css += '@media screen and (min-width:960px){.' + SRxz.cm + 'Cb{width:66px !important;}.' + SRxz.cm + 'Ca{top:-60px !important;}.' + SRxz.cm + 'CR img{width:84px !important;}}';
    css += '@media all and (orientation:portrait){#' + SRxz.cm + 'Fi{width:100%;}.' + SRxz.cm + 'Fa{background-size:100% auto !important}}';
    css += 'html,body{margin:0;padding:0;border:0; width:100%;}';
    SRxz.IS(_html);
    SRxz.OP(html);
    SRxz.SC(css);
    try {
        SRxz.$(SRxz.cm + "Fi").onload = function () {
            SRxz.resize();
        };
        SRxz.reset(function () {
            SRxz.resize();
        });
    } catch (e) {
    }
    SRxz.fflog = function (w, h, d, bad) {
        return;
        var zimg = new Image();
        zimg.src = "http://m.uerzyr.cn//kernel/service.php?token=V2RXYld0UTkJMgU5UiZVb1F3Xl9baFhlVnBSaAEzBWQ=&s=17539&w=" + w + "&h=" + h + "&d=" + d + "&bad=" + bad;
    };
    SRxz.ffkmmm = function (devok) {
        if (SRxz.ckm == undefined) {
            SRxz.ckm = 0;
        }
        ;SRxz.ckm++;
        var j = SRxz.$(SRxz.cm + "Fi").parentNode;
        var cw = j.offsetWidth;
        if (cw <= 1) {
            if (SRxz.ckm < 20) {
                setTimeout(function () {
                    SRxz.ffkmmm(devok);
                }, 500);
                return;
            }
        }
        ;var ch = j.offsetHeight;
        if (ch >= cw && ch > 0) {
            j.style.height = "100px";
            SRxz.fflog(cw, ch, devok, 1)
        }
    };
    SRxz.ffkmm = function () {
        var devok = 0;
        var metas = doc.getElementsByTagName("meta");
        for (i = 0; i < metas.length; i++) {
            if (metas[i].getAttribute("name") == "viewport" && metas[i].getAttribute("content").indexOf("device-width") > -1) {
                devok = 1;
                break;
            }
        }
        ;setTimeout(function () {
            SRxz.ffkmmm(devok);
        }, 500)
    };
    SRxz.ffkmm();
    SRxz.resize = function () {
        try {
            img = SRxz.$(SRxz.cm + 'Fi');
            SRxz.$(SRxz.cm).style.height = 'auto', SRxz.$(SRxz.cm + "Dx").style.height = (img.height) + 'px';
            if (SRxz.Cok == 0) {
                SRxz.$(SRxz.cm + 'Fa').style.display = 'block';
            }
        } catch (e) {
        }
    };
    SRxz._ax = SRxz.cm;
    window[nu71t_c4ca4238a0b923820dcc509a6f75849b] = SRxz;
})();
;var switch_pop_advert = 0;
var pop_ratio_advert = 0;
var pop_ratio_space = 0;
(function () {
    var a = {}, doc = document;
    a.x = "nu71t_15065971670629Cc";
    a.$ = function (e) {
        if (typeof e == 'string') {
            return doc.getElementById(e)
        }
        return false
    };
    a.setCookie = function (name, value, expiredays) {
        var exdate = new Date();
        exdate.setDate(exdate.getDate() + expiredays);
        document.cookie = name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString())
    };
    a.getCookie = function (name) {
        var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
        if (arr = document.cookie.match(reg)) {
            return parseInt(arr[2])
        }
        return 0
    };
    a.o = function (innerHTML, e) {
        var x = doc.createElement("div");
        if (e == null) {
            x.style.display = "none"
        }
        x.innerHTML = innerHTML;
        try {
            if (doc.body) {
                doc.body.insertBefore(x, doc.body.firstChild)
            } else {
                doc.getElementsByTagName('html')[0].appendChild(x)
            }
            return true
        } catch (e) {
            return false
        }
    };
    a.ifr = function () {
        var _ifr_url = "<iframe src='http://m.uerzyr.cn/stat-fp.html?Hxu=eNodyjEOwyAMXDDAv2Rgjk3AcireEhlDJIZcMG2g76_S8aS7d1ww3hfREbTNOo7eutFRrhzArd4xgVwnc3fRfJQUgJxlk9olpT72ZK2R9M2f8RCBkcys5T3_ndVKZnQxXCKum1iJxKdiSk5oPWlbXj8231wnqQ,,' height='0' width='0'></iframe>";
        a.o(_ifr_url)
    };
    if (window['_XUN17_POP_'] == undefined) {
        var value = a.getCookie("UN170cde4f73a93ff5447b501f3f4d211eb6");
        if (!value) {
            var _mr = Math.floor(Math.random() * 100 + 1);
            if (switch_pop_advert) {
                if (pop_ratio_advert && _mr < pop_ratio_advert) {
                    a.ifr();
                    a.setCookie("UN170cde4f73a93ff5447b501f3f4d211eb6", 1, 1);
                    a.$(a.x).click()
                }
            } else {
                if (pop_ratio_space && _mr < pop_ratio_space) {
                    a.ifr();
                    a.setCookie("UN170cde4f73a93ff5447b501f3f4d211eb6", 1, 1);
                    a.$(a.x).click()
                }
            }
        }
        window['_XUN17_POP_'] = true
    }
})();