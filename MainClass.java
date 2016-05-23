package com.sist.beer.dao;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainClass {
	public static void main(String[] args){
		BeerDAO dao = new BeerDAO();
		
		List<BeerVO> list = new ArrayList<BeerVO>();
		for(int i=149;i<=184;i++){
			try{
				
					Document doc = Jsoup.connect("http://new.cinefox.com/vod/movie/list?page="+i).get();
					Elements movieNO = doc.select("div.listBox ul.list li div.postimg");
					System.out.println(i+"번째 돌아가는 중입니다.");
					
					for(int j=0;j<movieNO.size();j++){
						try{
						Element noelem = movieNO.get(j);
						String no1 = noelem.attr("onclick");
						no1 = no1.substring(no1.lastIndexOf('=')+1,no1.lastIndexOf(")")-1);
						
						Document doc1 = Jsoup.connect("http://new.cinefox.com/vod/view?product_seq="+no1).get();
						Elements titleElem = doc1.select("title");
						Elements posterElem = doc1.select("img#PIMG");
						Elements ratingElem = doc1.select("div.metaInfoWrap div:eq(0)");
						Elements directorElem = doc1.select("div.metaInfoWrap div:eq(1)");
						Elements synopsisElem = doc1.select("div#content");
						Elements gradeElem = doc1.select("span.startNum");
						String title = titleElem.text();
						title = title.substring(6);   // title
						String poster = posterElem.attr("src");   // poster
						
						String director = directorElem.text();
						if(director.substring(0,director.lastIndexOf("|")-1).length()>6)
							director = director.substring(director.indexOf(":")+2, director.lastIndexOf("|"));  // director
						else
							director = "";
						String actor = directorElem.text();
						if(actor.substring(actor.lastIndexOf(":")).length()>9)
							if(actor.contains("다운로드"))
								actor = actor.substring(actor.lastIndexOf(":")+2,actor.lastIndexOf(" "));  // actor
							else
								actor = actor.substring(actor.lastIndexOf(":")+2);  // actor
						else
							actor = "";
						String time = ratingElem.text();
						time = time.substring(0,time.indexOf("|")); // time
						String synopsis = synopsisElem.text(); // synopsis
						String token = ratingElem.text();
						StringTokenizer st = new StringTokenizer(token, "|");
						st.nextToken();
						st.nextToken();
						String genre = st.nextToken(); // genre
						String temp = st.nextToken(); // playdate
						String playdate = temp;
						String rating = st.nextToken();
						if(!temp.contains("개봉")){
							playdate = "";
							rating = temp;
						}
						if(rating.contains("바로")){
							rating = rating.substring(0,rating.lastIndexOf("가")+1);
						}
						
						String strGrade = gradeElem.text();
						if(strGrade.trim()==null || strGrade.equals(""))
							strGrade = "5.0";
						double grade = Double.parseDouble(strGrade.trim()); // grade
						
						BeerVO vo = new BeerVO();
						vo.setNo(Integer.parseInt(no1));
						vo.setTitle(title);
						vo.setPoster(poster);
						vo.setRating(rating);
						vo.setDirector(director);
						vo.setActor(actor);
						vo.setTime(time);
						vo.setSynopsis(synopsis);
						vo.setGenre(genre);
						vo.setPlaydate(playdate);
						vo.setGrade(grade);
						dao.beerInsert(vo);
						}catch(Exception ex){
							System.out.println("두번째 포문"+j+"번째 에러");
						}
					}
					
	//			
				
			}catch(Exception ex){
				ex.printStackTrace();
				System.out.println(i+"번째 에러"+ex.getMessage());
			}
		}
	}
}
