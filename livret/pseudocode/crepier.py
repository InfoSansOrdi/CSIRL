def trier(tas_non_trié):
    inserer_pelle(dessous(la_plus_grande_crepe(tas_non_trié)))
    retourner()
    if face_peinte_visible(la_crepe_du_dessus):
        inserer_pelle(dessous(la_crepe_du_dessus))
        retourner()
    inserer_pelle(dessous(tas_non_trié))
    retourner()
    # nouveau_tas_non_trié == tas_non_trié moins celle que l'on vient de mettre en place
    trier(nouveau_tas_non_trié)
