import random
def a_mon_tour(nombre_d_allumettes):
    reste = nomdre_d_allumettes % 4 # modulo, ie reste de la division entière
    if reste == 0:
        # je perds à tous les coups
        prendre(random.randint(1,3))
    else:
        prendre(reste)
