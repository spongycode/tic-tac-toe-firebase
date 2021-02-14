import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.spongycode.tictactoe.Blogs
import com.spongycode.tictactoe.Friends
import com.spongycode.tictactoe.LiveGames

@Suppress("DEPRECATION")
internal class TabLayoutAdapter(
        var context: Context,
        fm: FragmentManager,
        var totalTabs: Int) :
        FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                Blogs()
            }
            1 -> {
                LiveGames()
            }
            2 -> {
                Friends()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }

}