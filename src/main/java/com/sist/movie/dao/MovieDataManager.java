package com.sist.movie.dao;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

import java.io.*;
import java.net.*;
/*
 * String[] feel = {"사랑","로맨스","매력","즐거움","스릴",
				"소름","긴장","공포","유머","웃음","개그",
				"행복","전율","경이","우울","절망","신비",
				"여운","희망","긴박","감동","감성","휴머니즘",
				"자극","재미","액션","반전","비극","미스테리",
				"판타지","꿈","설레임","흥미","풍경","일상",
				"순수","힐링","눈물","그리움","호러","충격","잔혹"};
		String[] genre = {
				"드라마","판타지","공포","멜로","애정",
				"로맨스","모험","스릴러","느와르","다큐멘터리",
				"코미디","미스터리","범죄","SF","액션","애니메이션"	
		};
 */
public class MovieDataManager {
	public static void main(String[] args)
	{
		MovieDataManager m=new MovieDataManager();
		File file=new File("/home/sist/bigdataStudy/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MovieProject/desc.txt");
		if(file.exists())
			file.delete();
		
		for(int i=1;i<=3;i++)
		{
		  String json=m.movie_review("곡성", i);
		  m.json_parse(json);
		}
	}
    public static List<MovieDTO> movieAllData()
    {
    	List<MovieDTO> list=
    			new ArrayList<MovieDTO>();
    	try
    	{
    		Document doc=Jsoup.connect("http://www.cgv.co.kr/movies/").get();
    		//System.out.println(doc);
    		Elements titleElem=doc.select("div.box-contents strong.title");
    		Elements percentElem=doc.select("div.box-contents strong.percent");
    		Elements imgElem=doc.select("div.box-image span.thumb-image img");
    		
    		Elements likeElem=doc.select("div.box-contents span.like span.count strong i");
    		Elements rElem=doc.select("div.box-contents span.txt-info strong");
    		Elements sElem=doc.select("div.box-contents span.percent");
    		for(int i=0;i<titleElem.size();i++)
    		{
    			Element telem=titleElem.get(i);
    			Element pelem=percentElem.get(i);
    			Element ielem=imgElem.get(i);
    			Element lelem=likeElem.get(i);
    			String img=ielem.attr("src");
    			Element relem=rElem.get(i);
    			Element selem=sElem.get(i);
    			MovieDTO d=new MovieDTO();
    			d.setNo(i+1);
    			d.setTitle(telem.text());
    			d.setImage(img);
    			int like=Integer.parseInt(lelem.text().replace(",",""));
    			d.setLike(like);
    			//d.setRegdate(relem.text().substring(0, relem.text().indexOf("����")).trim());
    			//System.out.println(telem.text()+" "+pelem.text()+" "+img+" "+lelem.text()+" "+relem.text()+" "+selem.text());
    		    String percent=pelem.text();
    		    if(percent.equals("?"))
    		    	percent="0%";
    			d.setPercent(percent.substring(3, percent.indexOf('%')));
    		    //d.setReserve(Double.parseDouble(selem.text().substring(0, selem.text().indexOf('%'))));
    		    list.add(d);
    		}
    	}catch(Exception ex)
    	{
    		System.out.println(ex.getMessage());
    	}
    	return list;
    }
    public MovieDTO movieDetail(int no)
    {
    	List<MovieDTO> list=movieAllData();
    	MovieDTO d=list.get(no-1);
    	return d;
    }
	public List<MovieDTO> movieHot()
	{
		List<MovieDTO> list=new ArrayList<MovieDTO>();
		List<MovieDTO> temp=movieAllData();
		int[] rand=new int[2];
		for(int i=0;i<2;i++)
		{
			rand[i]=(int)(Math.random()*(temp.size()));
			MovieDTO d=temp.get(rand[i]);
			list.add(d);
		}
		
		return list;
	}
	public List<String> movie_rank()
	{
		List<String> list=new ArrayList<String>();
		try
		{
			Document doc=Jsoup.connect("http://movie.naver.com/movie/sdb/rank/rmovie.nhn").get();
			Elements elems=doc.select("td.title div.tit3");
			for(int i=0;i<elems.size();i++)
			{
				if(i>9) break;
				Element td=elems.get(i);
				list.add(td.text());
			}
		}catch(Exception ex){}
		return list;
	}
	public List<String> movie_reserve()
	{
		List<String> list=new ArrayList<String>();
		try
		{
			Document doc=Jsoup.connect("http://movie.naver.com/movie/sdb/rank/rreserve.nhn").get();
			Elements elems=doc.select("td.title div.tit4");
			for(int i=0;i<elems.size();i++)
			{
				Element td=elems.get(i);
				list.add(td.text());
			}
		}catch(Exception ex){}
		return list;
	}
	public List<String> movie_boxoffice()
	{
		List<String> list=new ArrayList<String>();
		try
		{
			Document doc=Jsoup.connect("http://movie.naver.com/movie/sdb/rank/rboxoffice.nhn").get();
			Elements elems=doc.select("td.title div.tit1");
			for(int i=0;i<elems.size();i++)
			{
				Element td=elems.get(i);
				list.add(td.text());
			}
		}catch(Exception ex){}
		return list;
	}
	// 7b429affa32c43e1adf62ad1eebb6928
	
	public String movie_review(String title,int page)
	{
		StringBuffer sb=new StringBuffer();
		try
		{
			String key="80411406ba4ab7fc579820a0960441fc";
			String query="https://apis.daum.net/search/blog?"
				   +"apikey="+key
				   +"&result=20&output=json&pageno="+page
				   +"&q="+URLEncoder.encode(title, "UTF-8");
			URL url=new URL(query);

			HttpURLConnection conn=
				(HttpURLConnection)url.openConnection();
			conn.connect();
			if(conn!=null)
			{
				BufferedReader in=
					new BufferedReader(
							new InputStreamReader(
									conn.getInputStream(), "UTF-8"));
				String data="";
				while(true)
				{
					data=in.readLine();
					if(data==null) break;
					sb.append(data+"\n");
				}
			}
			conn.disconnect();
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return sb.toString();
	}

	public void json_parse(String json)
	{
		try
		{
			JSONParser jc=new JSONParser();
			JSONObject jo=(JSONObject)jc.parse(json);
			JSONObject channel=(JSONObject)jo.get("channel");
			JSONArray item=(JSONArray)channel.get("item");
			String desc="";
			for(int i=0;i<item.size();i++)
			{
				JSONObject obj=(JSONObject)item.get(i);
				String review=(String)obj.get("description");
				//System.out.println(review);
				desc+=review+"\n";
			}
			desc=desc.replaceAll("[A-Za-z0-9]", "");
			desc=desc.replace("&", "");
			desc=desc.replace(".", "");
			desc=desc.replace("#", "");
			desc=desc.replace("?", "");
			desc=desc.replace("/", "");
			desc=desc.replace(";", "");
			desc=desc.replace("(", "");
			desc=desc.replace(")", "");
			desc=desc.replace("[", "");
			desc=desc.replace("]", "");
			desc=desc.replace("+", "");
			desc=desc.replace(",", "");
			desc=desc.replace("'", "");
			desc=desc.replace("~", "");
			//desc=desc.replace("+", "");
			//System.out.println(desc);
			
			FileWriter fw=new FileWriter("/home/sist/bigdataStudy/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MovieProject/desc.txt",true);
			fw.write(desc);
			fw.close();
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	public void rGraph()
	{
		try
		{
			RConnection rc=new RConnection();
			rc.voidEval("review<-readLines(\"/home/sist/bigdataStudy/MovieProject/output/part-r-00000\")");
			rc.voidEval("png(\"/home/sist/bigdataStudy/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MovieProject/recommand.png\",width=800,height=600)");
			rc.voidEval("dev.off()");
			rc.close();
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
	}
	public List<String> read_review()
	{
		List<String> list=new ArrayList<String>();
		try
		{
			RConnection rc=new RConnection();
			rc.setStringEncoding("utf8");
			rc.voidEval("review<-readLines(\"/home/sist/bigdataStudy/.metadata/.plugins/org.eclipse.wst.server.core/tmp0/wtpwebapps/MovieProject/desc.txt\")");
			rc.voidEval("par(mfrow=c(1,2))");
			rc.voidEval("pie()");
			rc.voidEval("varplot()");
			REXP p=rc.eval("review");
			String[] data=p.asStrings();
			for(String s:data)
			{
				list.add(s);
			}
		}catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		return list;
	}

}