package controllers

import common._
import javax.swing.JFrame
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener


object Home extends JFrame {
  this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
  this.setLayout(new BorderLayout)
  this.setSize(800, 500)
  
  var northPanel = new JPanel
  northPanel.setLayout(new FlowLayout)
  this.add(northPanel, BorderLayout.NORTH)
  
  var cartPanel = new JPanel
  cartPanel.setLayout(new FlowLayout(FlowLayout.LEFT))
  northPanel.add(cartPanel)
  var createButton = new JButton("Create cart")
  createButton.addActionListener(new ActionListener(){
    override def actionPerformed(e: ActionEvent){
      createCart()
    }
  })
  cartPanel.add(createButton)
  
  var checkoutButton = new JButton("Checkout")
  checkoutButton.addActionListener(new ActionListener(){
    override def actionPerformed(e: ActionEvent){
      checkoutCart()
    }
  })
  cartPanel.add(checkoutButton)
  
  var logPanel = new JPanel
  logPanel.setLayout(new FlowLayout(FlowLayout.RIGHT))
  cartPanel.add(logPanel)
  
  var loginButton = new JButton("Log in")
  loginButton.addActionListener(new ActionListener(){
    override def actionPerformed(e: ActionEvent){
      login()
    }
  })
  logPanel.add(loginButton)
  
  var logoutButton = new JButton("Log out")
  logoutButton.addActionListener(new ActionListener(){
    override def actionPerformed(e: ActionEvent){
      logout()
    }
  })
  logPanel.add(logoutButton)
  
  this.setVisible(true)
  
  def createCart(){
    println("Create cart")
  }
  
  def logout(){
    println("logout")
  }
  
  def checkoutCart(){
    println("Checkout cart")
  }
  
  def login(){
    println("login")
  }
}

object Main extends App{
    override def main(args: Array[String]){
      Home
    }
}