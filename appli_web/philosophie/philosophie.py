#!/usr/bin/python3
# -*- coding: utf-8 -*-

# Ne pas se soucier de ces imports
import setpath
from flask import Flask, render_template, session, request, redirect, flash
from getpage import getPage


app = Flask(__name__)

app.secret_key = "TODO: mettre une valeur secrète ici"


@app.route('/', methods=['GET'])
def index(): 
    return render_template('index.html')

# Si vous définissez de nouvelles routes, faites-le ici
@app.route('/new-game', methods=['POST']) 
def new_game():
	session['article'] = request.form['page']  
	session['score'] = 0
	if(session['article']=='Philosophie'):
		return  render_template('index.html')
	else:
		return  redirect('/game' )

@app.route('/game', methods=['GET']) 
def game():  
	# session['title'] , session['content'] =   getPage(session['article']) 
    # return  render_template('game.html')  
	try:

		session['title'] , session['content'] =   getPage(session['article']) 
		# si contenue vide, amène à aucune page
		if not session['content']:
			flash('It does not lead to other pages, you loose','warning') 
			return render_template('index.html') 
		else:
			session['new_score']=session['score'] 
			return  render_template('game.html') 
	# la page de départ amène nulle part    
	except TypeError:
		flash('The page you request does not exist','error') 
		return  render_template('index.html') 

    
    #return  render_template('game.html') 


@app.route('/move', methods=['POST']) 
def move():  
     
    session['article'] = request.form['choix']
    session['score'] = session['score']+1
 
    current_score = int(request.form['score_current'])+1
    current_article = request.form['article_current']
    flash(int(current_score))
    flash(current_article) 
    if(current_score!=session['score']):
        flash("Vous avez ouvert le jeux dans un autre onglet, fermer un des onglets") 
        # on ignore les mouvements 
        current_score = session['score']
        return  redirect('/game' )
 
    if(session['article'] != 'Philosophie' ):
        #if(current_score!=session['score']):
         #   flash("Vous avez ouvert le jeux dans un autre onglet, fermer un des onglets")
         #   return  redirect('/game' )
        #else:
        return  redirect('/game' )
    else:
        flash('You win, with a score of :' + str(session['score']),'success') 
        return  render_template('index.html')



if __name__ == '__main__':
    app.run(debug=True, port=8000)

