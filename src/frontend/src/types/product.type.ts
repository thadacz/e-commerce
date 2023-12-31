import Category from "./category.type";

export default interface IProduct {
  id?: any | null;
  name: string;
  description: any;
  price: number;
  stock: number;
  image: File | null;
  category?: Category | null;
}



