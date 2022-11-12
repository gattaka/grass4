package cz.gattserver.grass.articles.plugins.templates;


import cz.gattserver.grass.articles.editor.parser.impl.ContextImpl;
import cz.gattserver.grass.articles.plugins.templates.plotter.PlotterElement;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class PlotterElementTest {

    @Test
    public void test() {
        PlotterElement e = new PlotterElement(1.1, 2.2, 3.2, 45.5, "sin(x) * 2", null, null) {
            @Override
            protected UUID generateRandomUUID() {
                return UUID.fromString("8e6f402c-5432-4dca-9de7-f787f863fa20");
            }
        };
        ContextImpl ctx = new ContextImpl();
        e.apply(ctx);
        String out = ctx.getOutput();
        assertEquals(
                "<div style=\"margin: 0px 5px; display: inline-block;\"><canvas id=\"can8e6f402c_5432_4dca_9de7_f787f863fa20\" style=\"cursor: pointer; \"></canvas></div>",
                out);
        assertEquals(1, ctx.getJSCodes().size());
        assertEquals(
                "$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20=function(){let e,t,n,l,o,i,r,f,c=document.getElementById(\"can8e6f402c_5432_4dca_9de7_f787f863fa20\"),u=c.getContext(\"2d\"),a=c.offsetWidth,s=c.offsetHeight,d=!1,g=1.1,v=2.2,h=g,y=v,m=Math.ceil(a/50),T=Math.ceil(s/50),k=function(x){return sin(x) * 2},b=function(){n=m*g,l=T*v,o=50/g,i=50/v},p=function(){b(),g=h,v=y,r=-3.2*o,f=45.5*i,M()},E=function(e){return Number(e.toPrecision(2))},M=function(){u.clearRect(0,0,a,s),u.strokeStyle=\"grey\",u.fillStyle=\"grey\";u.font=\"11px Monospace\",u.strokeRect(0,0,a,s);b();let e=Math.floor(r/50),t=Math.floor(f/50),l=a/2+r,c=s/2+f;u.strokeStyle=\"#ddd\",u.fillStyle=\"grey\",u.textAlign=\"left\";let d=Math.floor(m/2)+e,h=l-50*d;for(let e=-1;e<=m;e++){let t=g*(e-d),n=h+50*e,l=n+6;u.beginPath(),u.moveTo(n,0),u.lineTo(n,s),u.stroke(),u.textAlign=\"left\",u.fillText(E(t),l,c-6)}let y=t-Math.floor(T/2),p=c-50*y;for(let e=0;e<=T;e++){let t=v*(y+e),n=p-50*e;u.beginPath(),u.moveTo(0,n),u.lineTo(a,n),u.stroke();let o=n-6;u.fillText(E(t),l+6,o)}u.strokeStyle=\"grey\",u.fillStyle=\"grey\",u.beginPath(),u.moveTo(0,c),u.lineTo(a,c),u.stroke(),u.beginPath(),u.moveTo(l,0),u.lineTo(l,s),u.stroke();let M=-n/2-e*g,S=M+n,x=1/o;for(let e=M-g;e<=S;e+=x){let t=e*o+l,n=c-k(e)*i;u.fillStyle=\"blue\",u.fillRect(t-1,n-1,2,2)}};return c.addEventListener(\"wheel\",function(e){e.preventDefault();e.deltaY>0?(g*=1.1,v*=1.1):(g/=1.1,v/=1.1),M()}),c.addEventListener(\"mousedown\",function(n){d=!0,e=n.clientX,t=n.clientY},!1),c.addEventListener(\"mouseup\",function(e){d=!1},!1),c.addEventListener(\"mouseleave\",function(e){d=!1},!1),c.addEventListener(\"mousemove\",function(n){d&&(r-=e-n.clientX,f-=t-n.clientY,e=n.clientX,t=n.clientY,M())},!1),c.addEventListener(\"dblclick\",function(e){e.preventDefault(),p()},!1),{start:function(){p()}}}(),$.plotter8e6f402c_5432_4dca_9de7_f787f863fa20.start();",
                ctx.getJSCodes().iterator().next());
    }

}
