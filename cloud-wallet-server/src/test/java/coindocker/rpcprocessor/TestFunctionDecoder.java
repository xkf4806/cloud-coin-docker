package coindocker.rpcprocessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * @author think on 19/1/2018
 */
public class TestFunctionDecoder {

  public static void main(String[] args) {
    String input = "0xa9059cbb0000000000000000000000007ec6064b6f21be41ca4f9f9c8fabacea08daf1aa0000000000000000000000000000000000000000000000009724ebd751c26051";
    List<TypeReference<?>> typeReferences = Arrays.asList(
         new TypeReference<Address>() {
        }, new TypeReference<Uint256>() {
        });
    List<Type> result = FunctionReturnDecoder.decode(input, convert(typeReferences));
    result.forEach(type -> System.out.println(type.getValue()));
  }

  @SuppressWarnings("unchecked")
  public static List<TypeReference<Type>> convert(List<TypeReference<?>> input) {
    List<TypeReference<Type>> result = new ArrayList<>(input.size());
    result.addAll(input.stream()
        .map(typeReference -> (TypeReference<Type>) typeReference)
        .collect(Collectors.toList()));
    return result;
  }
}
