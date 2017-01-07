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
  
  var cartPanel = new JPanel
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
  
  var logPanel = new JPanel
  
  def createCart(){
    
  }
  
  def checkoutCart(){
    
  }
  
}