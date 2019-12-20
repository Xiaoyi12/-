
import java.util.Collection;

import com.briup.environment.bean.Environment;
import com.briup.environment.util.Configuration;
import com.briup.environment.util.ConfigurationImpl;

public class GatherTest {

	public static void main(String[] args) {
		Configuration configuration = new ConfigurationImpl();
		try {
			Collection<Environment> collection = (Collection<Environment>) configuration.getGather().gather();
			System.out.println(collection);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

}
