#!/usr/bin/python3
# -*- coding: utf-8 -*-

# Ne pas se soucier de ces imports
import setpath
from bs4 import BeautifulSoup
from json import loads
from urllib.request import urlopen
from urllib.parse import urlencode
from pprint import pprint

# Si vous écrivez des fonctions en plus, faites-le ici
cache = {}


def getJSON(page):
    """
    page => page dans wiki
    """
    params = urlencode({
      'format': 'json',  # TODO: compléter ceci
      'action': 'parse',  # TODO: compléter ceci
      'prop': 'text',  # TODO: compléter ceci
      'redirects' : True,
      'page':  page}) 
    API = "https://fr.wikipedia.org/w/api.php"  # TODO: changer ceci
    response = urlopen(API + "?" + params)

    return response.read().decode('utf-8')
  
def getRawPage(page):
    parsed = loads(getJSON(page))
    try:
        title = parsed['parse']['title']   # TODO: remplacer ceci
        content = parsed['parse']['text']['*'] # TODO: remplacer ceci
        return title, str(content)
    except KeyError:
        # La page demandée n'existe pas
        return None, None



def getPage1(page):
    
    try: 
        parsed = loads(getJSON(page))
#         redirect_title= parsed['parse']['redirects'][0]['to']  
        title_tmp, content_tmp = getRawPage(page) 
        soup = BeautifulSoup(content_tmp, 'html.parser')
        liste_href=[] 
            
        for link in soup.find_all('p', recursive = False):
            for i in link.find_all('a'):
                liste_href.append(i.get('href'))
                
        # retire les lien externe
        for i in liste_href:
            if("/wiki/" not in i): 
                liste_href.remove(i)
                
        # retire les wiki du debut
        liste_href2=[]
        for i in liste_href: 
            #liste_href2.append(i[6:])
            liste_href2.append( urldefrag(unquote(i[6:]))[0] )
        return title_tmp, liste_href2[:10]

        # ajout des trucs qui marchen plus

    except KeyError:
        return None, []
 

from urllib.parse import urldefrag
from urllib.parse import unquote
from collections import OrderedDict
'''
def getPagex(page):
    try: 
        parsed = loads(getJSON(page))
#         redirect_title= parsed['parse']['redirects'][0]['to']  
        title_tmp, content_tmp = getRawPage(page) 
        soup = BeautifulSoup(content_tmp, 'html.parser')
        liste_href=[] 
            
        for link in soup.find_all('p', recursive = False):
            for i in link.find_all('a'):
                liste_href.append(i.get('href'))
                
        # retire les liens externes
        for i in liste_href:
            if("/wiki/" not in i): 
                liste_href.remove(i)
            if("Aide:Référence" in unquote(i)): 
                liste_href.remove(i)
                
        # retire les wiki du debut
        liste_href2=[]
        for i in liste_href:  
            liste_href2.append( urldefrag(unquote(i[6:]))[0].replace('_',' '))

        # stock les liens dansune variable globale
        global cache
        cache[title_tmp] = list(OrderedDict.fromkeys( liste_href2[:10]))

        return title_tmp,list(OrderedDict.fromkeys( liste_href2[:10]))

    except KeyError:
        return None, []
'''
def getPage(page):
    """
    récupère le titre et le contenu HTML de la
    page avec la fonction getRawPage
    """
    global cache
    if(page in cache):
        return page, cache[page]
    else:
        try: 
           # parsed = loads(getJSON(page))
    #         redirect_title= parsed['parse']['redirects'][0]['to']  
            title_tmp, content_tmp = getRawPage(page) 
            soup = BeautifulSoup(content_tmp, 'html.parser')
            liste_href=[] 

            for link in soup.find_all('p', recursive = False):
                for i in link.find_all('a'):
                    liste_href.append(i.get('href'))

            # retire les lien externe
            for i in liste_href:
                if("/wiki/" not in i): 
                    liste_href.remove(i)
                if("Aide:Référence" in unquote(i)): 
                    liste_href.remove(i)
            # retire les liens rouges
            for i in liste_href:
                if("redlink" in i):
                    liste_href.remove(i)

            # retire les wiki du debut
            liste_href2=[]
            for i in liste_href:  
                #liste_href2.append( urldefrag(unquote(i[6:]))[0].replace('_',' '))
                liste_href2.append( urldefrag(unquote(i[6:]))[0] )

            liste_href3 = [w.replace('_', ' ') for w in liste_href2]
    #         global cache
            cache[title_tmp] = list(OrderedDict.fromkeys( liste_href3[:10]))
            return title_tmp,list(OrderedDict.fromkeys( liste_href3[:10]))

        except KeyError:
            flash('This page does not exist')
            return None, []

 


if __name__ == '__main__':
    # Ce code est exécuté lorsque l'on exécute le fichier
    print("Ça fonctionne !")
    
    # Voici des idées pour tester vos fonctions :
    # print(getJSON("Utilisateur:A3nm/INF344"))
    # print(getRawPage("Utilisateur:A3nm/INF344"))
    # print(getRawPage("Histoire"))