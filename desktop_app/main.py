import sys
from PyQt5.QtWidgets import QApplication, QWidget

def main():

    app = QApplication(sys.argv)
    w = QWidget()
    w.resize(500, 700)
    w.move(100, 100)
    w.setWindowTitle('Main')
    w.show()
    sys.exit(app.exec_())


if __name__ == '__main__':
    main()
