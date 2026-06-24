import "./globals.css";
import { Toaster } from "react-hot-toast";
export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {

  return (
    <html lang="pt">
      <body>

        {children}
        <Toaster />
      </body>
    </html>
  );
}