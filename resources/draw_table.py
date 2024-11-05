manche = 1
joueurs = {0: 1, 1: 0}

colors = [41, 42, 43, 44, 45, 46, 47, 100]


def point_or_points(n):
    if n == 1:
        return "point"
    else:
        return "points"


print("╔" + ("═" * 7) * 8 + ("═" * 7) + "╗")


# ║

def get_color(offset: int):
    return colors[offset % len(colors)]


def draw_line(index: int):
    # Because each case has a height of 3 and a width of 6
    for j in range(0, 3):
        if j == 1 and (index == 0 or index == 7):
            s = ""
            for k in range(0, 8):
                pawn = " "
                if index == 7:
                    pawn = "x"
                elif index == 0:
                    pawn = "o"

                c = get_color(index + k)
                if pawn != " ":
                    s += f"\u001b[{c}m  \u001b[0m {pawn} \u001b[{c}m  \u001b[0m║"
                else:
                    s += f"\u001b[{c}m       \u001b[0m║"
            print(f"║{s}")
        else:
            s = ""
            for k in range(0, 8):
                s += f"\u001b[{get_color(index + k)}m{' ' * 7}\u001b[0m║"

            if j != 2:
                print(f"║{s}")
            else:
                print(f"║{s}", end="")


for i in range(0, 8):
    draw_line(i)

    match i:
        case 0:
            print(f"   Manche {manche}", end="")
        case 1:
            print(f"     \u001b[34m[Joueur 1]\u001b[0m - {joueurs[0]} {point_or_points(joueurs[0])}", end="")
        case 2:
            print(f"     \u001b[34m[Joueur 2]\u001b[0m - {joueurs[1]} {point_or_points(joueurs[1])}", end="")

    print()

print("╚" + ("═" * 7) * 8 + ("═" * 7) + "╝")
