package kr.ac.inhagachon.www.idol;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper extends AppCompatActivity{ //파일을 전체적으로 관리하는 클래스

    public void Create_New_Account(String name, String ID, String password, String birth) { // 새로운 계정 생성
        File Account_File=new File(getFilesDir()+Load.Account_File_name);
        try { //파일에 기록
            BufferedWriter bw=new BufferedWriter(new FileWriter(Account_File, true));
            bw.write(name);
            bw.newLine();
            bw.write(birth);
            bw.newLine();
            bw.write(ID);
            bw.newLine();
            bw.write(password);
            bw.newLine();
            bw.flush();
            bw.close();
            Toast.makeText(getApplicationContext(), "계정이 생성되었습니다", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            try {
                Account_File.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "파일을 읽을 수 없습니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        //인스턴스 생성
        Load.accounts[Account.count++]=new Account(name, birth, ID, password);
    }

}
