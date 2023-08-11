import { Routes, Route } from "react-router-dom"
import { Container } from "react-bootstrap"
import { Store } from "./pages/Store"
import { Navbar } from "./components/Navbar"
import { ShoppingCartProvider } from "./context/ShoppingCartContext"
import { Cart } from "./pages/Cart"
import Login from "./pages/Login";
import  Registration  from "./pages/Registration"
import DeliveryForm from "./components/DeliveryForm";
import Payment from "./components/Payment";
import AddProduct from "./components/AddProduct"
import ProductsList from "./components/ProductList"
import Product from "./components/Product"
import Completion from "./pages/Completion"

function App() {
  
  return (
    <ShoppingCartProvider>
      <Navbar />
      <Container className="mb-4">
        <Routes>
          <Route path="/" element={<Store />} />
          <Route path="/cart" element={<Cart />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registration" element={<Registration />} />
          <Route path="/delivery-form" element={<DeliveryForm />} />
          <Route path="/payment" element={<Payment />} />
          <Route path="/add" element={<AddProduct />} />
          <Route path="/products" element={<ProductsList />} />
          <Route path="/products/:id" element={<Product />} />
          <Route path="/completion" element={<Completion />} />
        </Routes>
      </Container>
    </ShoppingCartProvider>
  );
}

export default App
